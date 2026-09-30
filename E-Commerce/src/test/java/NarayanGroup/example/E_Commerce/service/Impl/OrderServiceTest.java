package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.IRazorpayService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private final IOrderRepository orderRepository=mock(IOrderRepository.class);
    private final IPaymentRepository paymentRepository=mock(IPaymentRepository.class);
    private final IRazorpayService razorpay=mock(IRazorpayService.class);
    private final DomainEventProducer eventProducer=mock(DomainEventProducer.class);
    private final OrderService service=new OrderService(orderRepository,paymentRepository,razorpay,eventProducer);

    private Order order(OrderStatus status,String method,String paymentStatus) {
        UserEntity user=UserEntity.builder().id(1L).email("u@test.com").build();
        Product product=Product.builder().id(10L).title("Phone").build();
        Order o=Order.builder().id(100L).orderNumber("ORD-100").user(user).status(status).paymentMethod(method)
                .paymentStatus(paymentStatus).subtotal(new BigDecimal("100")).tax(BigDecimal.ZERO).deliveryCharge(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO).totalAmount(new BigDecimal("100")).currency("INR")
                .shippingFullName("User").shippingPhone("9876543210").shippingAddressLine1("Line")
                .shippingCity("Noida").shippingState("UP").shippingPincode("201301")
                .createdAt(LocalDateTime.now().minusHours(1)).updatedAt(LocalDateTime.now()).orderItems(new java.util.ArrayList<>()).build();
        o.getOrderItems().add(OrderItem.builder().id(1L).order(o).product(product).productTitle("Phone").quantity(1L)
                .unitPrice(new BigDecimal("100")).totalPrice(new BigDecimal("100")).currency("INR").build());
        return o;
    }
    private Payment payment(Order o,String method,String status) {
        return Payment.builder().id(1L).paymentId("PAY-1").order(o).gatewayPaymentId("pay_1").amount(new BigDecimal("100"))
                .currency("INR").paymentMethod(method).status(status).build();
    }

    @Test void getOrdersAndGetOrder_shouldMapData() {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
        when(orderRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(1L)).thenReturn(List.of(o));
        assertEquals(1,service.getOrders(1L).size());
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o));
        var dto=service.getOrder(1L,100L); assertEquals("ORD-100",dto.getOrderNumber()); assertEquals(1,dto.getItems().size());
    }

    @Test void getOrder_shouldThrowWhenMissing() {
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.empty());
        assertThrows(CustomException.OrderNotFoundException.class,()->service.getOrder(1L,100L));
    }

    @Test void cancelOrder_shouldReturnForAlreadyCancelled() {
        Order o=order(OrderStatus.CANCELLED,CommonConstants.COD,"CANCELLED");
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o));
        service.cancelOrder(1L,100L,"reason"); verifyNoInteractions(paymentRepository);
    }

    @Test void cancelOrder_shouldRejectShippedOrDelivered() {
        for(OrderStatus status: List.of(OrderStatus.SHIPPED,OrderStatus.DELIVERED)) {
            Order o=order(status,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
            when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o));
            assertThrows(CustomException.CheckoutException.class,()->service.cancelOrder(1L,100L,null));
        }
    }

    @Test void cancelOrder_shouldRefundPaidRazorpayAndPublishEvent() throws Exception {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.RAZORPAY,CommonConstants.PAYMENT_STATUS_SUCCESS); Payment p=payment(o,CommonConstants.RAZORPAY,CommonConstants.PAYMENT_STATUS_SUCCESS);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o)); when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(p));
        service.cancelOrder(1L,100L," customer request ");
        assertEquals(OrderStatus.CANCELLED,o.getStatus()); assertEquals(CommonConstants.PAYMENT_STATUS_REFUNDED,p.getStatus());
        verify(razorpay).refundPayment("pay_1",new BigDecimal("100"),"INR"); verify(eventProducer).publishOrderCancelled(any());
    }

    @Test void cancelOrder_shouldCancelUnpaidPaymentWithDefaultReason() {
        Order o=order(OrderStatus.PENDING_PAYMENT,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING); Payment p=payment(o,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o)); when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(p));
        service.cancelOrder(1L,100L," ");
        assertEquals("CANCELLED",p.getStatus()); assertEquals("CANCELLED",o.getPaymentStatus()); verify(eventProducer).publishOrderCancelled(any());
    }

    @Test void cancelOrder_shouldWrapRefundFailure() throws Exception {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.RAZORPAY,CommonConstants.PAYMENT_STATUS_SUCCESS); Payment p=payment(o,CommonConstants.RAZORPAY,CommonConstants.PAYMENT_STATUS_SUCCESS);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o)); when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(p));
        doThrow(new RuntimeException("gateway")).when(razorpay).refundPayment(anyString(),any(),anyString());
        assertThrows(CustomException.CheckoutException.class,()->service.cancelOrder(1L,100L,null));
    }

    @Test void cancelOrder_shouldRejectMissingPayment() {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o)); when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.empty());
        assertThrows(CustomException.CheckoutException.class,()->service.cancelOrder(1L,100L,null));
    }

    @Test void generateInvoice_shouldReturnPdfBytes() {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o));
        byte[] pdf=service.generateInvoice(1L,100L);
        assertNotNull(pdf); assertTrue(pdf.length>100); assertEquals((byte)'%',pdf[0]);
    }

    @Test void expirePendingPayment_shouldOnlyCancelExpiredPendingOrders() {
        Order o=order(OrderStatus.PENDING_PAYMENT,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING); o.setCreatedAt(LocalDateTime.now().minusHours(1));
        when(orderRepository.findByIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(o));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(payment(o,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING)));
        service.expirePendingPayment(100L);
        verify(orderRepository).save(o);
    }

//    @Test void expirePendingPayment_shouldReturnForNonPendingAndFreshOrders() {
//        Order confirmed=order(OrderStatus.CONFIRMED,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
//        when(orderRepository.findByIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(confirmed));
//        service.expirePendingPayment(100L);
//        verify(paymentRepository,never()).findByOrderIdAndIsDeletedFalse(anyLong());
//
//        Order fresh=order(OrderStatus.PENDING_PAYMENT,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_PENDING);
//        fresh.setCreatedAt(LocalDateTime.now());
//        when(orderRepository.findByIdAndIsDeletedFalse(101L)).thenReturn(Optional.of(fresh));
//        service.expirePendingPayment(101L);
//        verify(orderRepository,never()).save(fresh);
//    }

    @Test void cancelOrder_shouldRefundPaidNonRazorpayPaymentWithoutGatewayCall() {
        Order o=order(OrderStatus.CONFIRMED,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
        Payment p=payment(o,CommonConstants.COD,CommonConstants.PAYMENT_STATUS_SUCCESS);
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(100L,1L)).thenReturn(Optional.of(o));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(100L)).thenReturn(Optional.of(p));
        service.cancelOrder(1L,100L,null);
        assertEquals(CommonConstants.PAYMENT_STATUS_REFUNDED,p.getStatus());
        verifyNoInteractions(razorpay);
    }

}
