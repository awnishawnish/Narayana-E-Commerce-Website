package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryOrderService implements IDeliveryOrderService {
    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public ResponseMessageUtilityDTO assignDeliveryPartner(Long orderId, Long partnerUserId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        if (!CommonConstants.COD.equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalArgumentException("Delivery partner assignment is currently supported for COD orders only.");
        }
        UserEntity partner = userRepository.findById(partnerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery partner user not found."));
        if (partner.getRole() != Role.DELIVERY_PARTNER) {
            throw new IllegalArgumentException("Selected user does not have the DELIVERY_PARTNER role.");
        }
        if (order.getCodCollectedAt() != null) {
            throw new IllegalArgumentException("COD has already been collected for this order.");
        }
        order.setAssignedDeliveryPartnerId(partnerUserId);
        orderRepository.save(order);
        return response(200, "Delivery partner assigned successfully.", order.getId());
    }

    @Override
    @Transactional
    public ResponseMessageUtilityDTO confirmCodCollection(Long orderId, String authenticatedEmail) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            return response(401, "Authentication is required.", null);
        }
        UserEntity partner = userRepository.findByEmail(authenticatedEmail);
        if (partner == null || partner.getRole() != Role.DELIVERY_PARTNER) {
            return response(403, "A delivery-partner account is required.", null);
        }
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        if (!CommonConstants.COD.equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalArgumentException("This order is not a COD order.");
        }
        if (order.getCodCollectedAt() != null) {
            return response(200, "COD collection was already confirmed.", order.getCodCollectedAt());
        }
        if (order.getAssignedDeliveryPartnerId() == null || !order.getAssignedDeliveryPartnerId().equals(partner.getId())) {
            return response(403, "This order is not assigned to your delivery account.", null);
        }
        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found."));
        LocalDateTime collectedAt = LocalDateTime.now();
        order.setCodCollectedAt(collectedAt);
        order.setCodCollectedById(partner.getId());
        order.setPaymentStatus(CommonConstants.PAYMENT_STATUS_SUCCESS);
        order.setStatus(OrderStatus.DELIVERED);
        payment.setStatus(CommonConstants.PAYMENT_STATUS_SUCCESS);
        paymentRepository.save(payment);
        orderRepository.save(order);
        return response(200, "COD collection confirmed and order marked delivered.", collectedAt);
    }

    private ResponseMessageUtilityDTO response(int status, String message, Object data) {
        return ResponseMessageUtilityDTO.builder()
                .status(status >= 400 ? "ERROR" : CommonConstants.SUCCESS)
                .httpStatus(status).message(message).data(data).build();
    }
}
