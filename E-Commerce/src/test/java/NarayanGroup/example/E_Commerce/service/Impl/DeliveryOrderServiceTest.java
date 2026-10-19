package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryOrderServiceTest {
    private IOrderRepository orders;
    private IPaymentRepository payments;
    private IUserRepository users;
    private DeliveryOrderService service;

    @BeforeEach void setUp() {
        orders = mock(IOrderRepository.class);
        payments = mock(IPaymentRepository.class);
        users = mock(IUserRepository.class);
        service = new DeliveryOrderService(orders, payments, users);
    }

    @Test void assignPartnerRejectsMissingOrder() {
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assignDeliveryPartner(5L, 7L));
        verifyNoInteractions(users);
    }

    @Test void assignPartnerRejectsNonCodOrder() {
        Order order = order("PREPAID");
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(order));
        assertThrows(IllegalArgumentException.class, () -> service.assignDeliveryPartner(5L, 7L));
        verify(orders, never()).save(any());
    }

    @Test void assignPartnerRejectsWrongRole() {
        Order order = order(CommonConstants.COD);
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(order));
        when(users.findById(7L)).thenReturn(Optional.of(UserEntity.builder().id(7L).role(Role.USER).build()));
        assertThrows(IllegalArgumentException.class, () -> service.assignDeliveryPartner(5L, 7L));
    }

    @Test void assignPartnerSavesAssignedId() {
        Order order = order(CommonConstants.COD);
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(order));
        when(users.findById(7L)).thenReturn(Optional.of(UserEntity.builder().id(7L).role(Role.DELIVERY_PARTNER).build()));
        var result = service.assignDeliveryPartner(5L, 7L);
        assertEquals(200, result.getHttpStatus());
        assertEquals(7L, order.getAssignedDeliveryPartnerId());
        verify(orders).save(order);
    }

    @Test void collectionRequiresAuthentication() {
        var result = service.confirmCodCollection(5L, null);
        assertEquals(401, result.getHttpStatus());
        verifyNoInteractions(users, orders, payments);
    }

    @Test void collectionRejectsNonDeliveryPartner() {
        when(users.findByEmail("user@test.com")).thenReturn(UserEntity.builder().id(3L).role(Role.USER).build());
        var result = service.confirmCodCollection(5L, "user@test.com");
        assertEquals(403, result.getHttpStatus());
        verifyNoInteractions(orders, payments);
    }

    @Test void collectionIsIdempotentWhenAlreadyCollected() {
        Order order = order(CommonConstants.COD);
        order.setCodCollectedAt(java.time.LocalDateTime.now().minusMinutes(1));
        order.setAssignedDeliveryPartnerId(7L);
        when(users.findByEmail("partner@test.com")).thenReturn(UserEntity.builder().id(7L).role(Role.DELIVERY_PARTNER).build());
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(order));
        var result = service.confirmCodCollection(5L, "partner@test.com");
        assertEquals(200, result.getHttpStatus());
        assertTrue(result.getMessage().contains("already confirmed"));
        verifyNoInteractions(payments);
    }

    @Test void collectionMarksOrderAndPaymentDelivered() {
        Order order = order(CommonConstants.COD);
        order.setAssignedDeliveryPartnerId(7L);
        Payment payment = Payment.builder().id(9L).paymentId("pay-9").order(order)
                .amount(BigDecimal.TEN).currency("INR").paymentMethod(CommonConstants.COD).status("PENDING").build();
        when(users.findByEmail("partner@test.com")).thenReturn(UserEntity.builder().id(7L).role(Role.DELIVERY_PARTNER).build());
        when(orders.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(order));
        when(payments.findByOrderIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(payment));
        var result = service.confirmCodCollection(5L, "partner@test.com");
        assertEquals(200, result.getHttpStatus());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertEquals(CommonConstants.PAYMENT_STATUS_SUCCESS, order.getPaymentStatus());
        assertEquals(CommonConstants.PAYMENT_STATUS_SUCCESS, payment.getStatus());
        assertNotNull(order.getCodCollectedAt());
        verify(payments).save(payment);
        verify(orders).save(order);
    }

    private Order order(String paymentMethod) {
        return Order.builder().id(5L).orderNumber("ORDER-5")
                .paymentMethod(paymentMethod).paymentStatus("PENDING")
                .status(OrderStatus.PENDING_PAYMENT).subtotal(BigDecimal.TEN)
                .tax(BigDecimal.ZERO).deliveryCharge(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO).totalAmount(BigDecimal.TEN)
                .currency("INR").shippingFullName("Test User")
                .shippingPhone("9999999999").shippingAddressLine1("Line 1")
                .shippingCity("Delhi").shippingState("Delhi").shippingPincode("110001")
                .build();
    }
}
