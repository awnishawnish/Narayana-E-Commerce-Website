package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.ICartService;
import NarayanGroup.example.E_Commerce.service.IRazorpayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock IPaymentRepository paymentRepository;
    @Mock IOrderRepository orderRepository;
    @Mock DomainEventProducer eventProducer;
    @Mock IRazorpayService razorpayService;
    PaymentService service;
    @Mock
     ICartService cartService;


    @BeforeEach void setUp() {
        service = new PaymentService(paymentRepository, orderRepository, eventProducer, razorpayService,cartService);
        ReflectionTestUtils.setField(service, "razorpayKeySecret", "secret");
    }

    @Test void verify_shouldUpdatePaymentOrderAndPublishEvent() throws Exception {
        UserEntity user = UserEntity.builder().id(7L).email("user@test.com").build();
        Product product = Product.builder().id(10L).build();
        OrderItem item = OrderItem.builder().product(product).quantity(2L).build();
        Order order = Order.builder().id(1L).orderNumber("ORD-1").user(user).orderItems(java.util.Arrays.asList(item)).build();
        Payment payment = Payment.builder().paymentId(CommonConstants.PAYMENT_ID_PREFIX + "1").order(order).gatewayOrderId("order_1").paymentMethod(CommonConstants.RAZORPAY).status(CommonConstants.PAYMENT_STATUS_PENDING).amount(new java.math.BigDecimal("100.00")).currency("INR").build();
        String signature = sign("order_1|pay_1", "secret");
        PaymentVerificationRequestDTO request = PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1").razorpayPaymentId("pay_1").razorpaySignature(signature).build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L, 7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(razorpayService.isPaymentCaptured("pay_1", "order_1", new java.math.BigDecimal("100.00"), "INR")).thenReturn(true);

        Payment result = service.verifyRazorpayPayment(7L, request);

        assertSame(payment, result);
        assertEquals(CommonConstants.PAYMENT_STATUS_SUCCESS, payment.getStatus());
        assertEquals(CommonConstants.PAYMENT_STATUS_SUCCESS, order.getPaymentStatus());
        verify(eventProducer).publishPaymentSuccessful(any());
    }

    @Test void verify_shouldRejectInvalidSignature() throws Exception {
        Order order = Order.builder().id(1L).user(UserEntity.builder().id(7L).build()).build();
        Payment payment = Payment.builder().order(order).gatewayOrderId("order_1").paymentMethod(CommonConstants.RAZORPAY).status(CommonConstants.PAYMENT_STATUS_PENDING).build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L, 7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        PaymentVerificationRequestDTO request = PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1").razorpayPaymentId("pay_1").razorpaySignature("bad").build();
        assertThrows(RuntimeException.class, () -> service.verifyRazorpayPayment(7L, request));
        verifyNoInteractions(eventProducer);
    }

//    @Test void verify_shouldBeIdempotentWhenAlreadySuccessful() {
//        Payment payment = Payment.builder().status("SUCCESS").paymentMethod(CommonConstants.RAZORPAY).build();
//        Order order = Order.builder().id(1L).user(UserEntity.builder().id(7L).build()).build();
//        payment.setOrder(order);
//        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L, 7L)).thenReturn(Optional.of(order));
//        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
//        PaymentVerificationRequestDTO request = PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1").razorpayPaymentId("pay_1").razorpaySignature("ignored").build();
//        assertSame(payment, service.verifyRazorpayPayment(7L, request));
//        verifyNoInteractions(eventProducer);
//    }

    private static String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    @Test void verify_shouldReturnAlreadySuccessfulPaymentWithoutGatewayCall() {
        UserEntity user=UserEntity.builder().id(7L).build();
        Order order=Order.builder().id(1L).user(user).build();
        Payment payment=Payment.builder().order(order).paymentMethod(CommonConstants.RAZORPAY)
                .status(CommonConstants.PAYMENT_STATUS_SUCCESS).gatewayOrderId("order_1").build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L,7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        PaymentVerificationRequestDTO request=PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1")
                .razorpayPaymentId("pay_1").razorpaySignature("ignored").build();
        assertSame(payment,service.verifyRazorpayPayment(7L,request));
        verifyNoInteractions(razorpayService,eventProducer);
    }

    @Test void verify_shouldRejectWrongMethodAndOrderAndCapturedFailure() {
        UserEntity user=UserEntity.builder().id(7L).build(); Order order=Order.builder().id(1L).user(user).build();
        Payment payment=Payment.builder().order(order).paymentMethod(CommonConstants.COD).status(CommonConstants.PAYMENT_STATUS_PENDING).gatewayOrderId("order_1").build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L,7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        PaymentVerificationRequestDTO req=PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1").razorpayPaymentId("pay").razorpaySignature("x").build();
        assertThrows(RuntimeException.class,()->service.verifyRazorpayPayment(7L,req));

        payment.setPaymentMethod(CommonConstants.RAZORPAY); req.setRazorpayOrderId("other");
        assertThrows(RuntimeException.class,()->service.verifyRazorpayPayment(7L,req));
    }

    @Test void verify_shouldRejectWhenGatewayPaymentNotCaptured() throws Exception {
        UserEntity user=UserEntity.builder().id(7L).build(); Product product=Product.builder().id(10L).build();
        OrderItem item=OrderItem.builder().product(product).quantity(1L).build();
        Order order=Order.builder().id(1L).user(user).orderItems(java.util.List.of(item)).build();
        Payment payment=Payment.builder().order(order).paymentMethod(CommonConstants.RAZORPAY).status(CommonConstants.PAYMENT_STATUS_PENDING)
                .gatewayOrderId("order_1").amount(new java.math.BigDecimal("100")).currency("INR").build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L,7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        when(razorpayService.isPaymentCaptured(anyString(),anyString(),any(),anyString())).thenReturn(false);
        String signature=sign("order_1|pay_1","secret");
        var req=PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("order_1").razorpayPaymentId("pay_1").razorpaySignature(signature).build();
        assertThrows(RuntimeException.class,()->service.verifyRazorpayPayment(7L,req));
    }

    @Test void markPaymentFailed_shouldPublishAndHandleIdempotentStates() {
        UserEntity user=UserEntity.builder().id(7L).email("u@test.com").build();
        Order order=Order.builder().id(1L).orderNumber("ORD-1").user(user).orderItems(java.util.List.of()).build();
        Payment payment=Payment.builder().order(order).paymentId("PAY-1").paymentMethod(CommonConstants.RAZORPAY).status(CommonConstants.PAYMENT_STATUS_PENDING).build();
        when(orderRepository.findByIdAndUserIdAndIsDeletedFalse(1L,7L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        assertSame(payment,service.markRazorpayPaymentFailed(7L,1L,""));
        assertEquals(CommonConstants.PAYMENT_STATUS_FAILED,payment.getStatus()); assertEquals(CommonConstants.PAYMENT_STATUS_FAILED,order.getPaymentStatus());
        verify(eventProducer).publishPaymentFailed(any());

        payment.setStatus(CommonConstants.PAYMENT_STATUS_SUCCESS);
        assertSame(payment,service.markRazorpayPaymentFailed(7L,1L,"ignored"));
        verify(paymentRepository,times(1)).save(payment);
    }

}
