package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery/orders")
public class DeliveryOrderController {
    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IUserRepository userRepository;

    @PostMapping("/{orderId}/assign/{partnerUserId}")
    @Transactional
    public ResponseEntity<ResponseMessageUtilityDTO> assignDeliveryPartner(
            @PathVariable Long orderId, @PathVariable Long partnerUserId) {
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
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS).httpStatus(200)
                .message("Delivery partner assigned successfully.").data(order.getId()).build());
    }

    @PostMapping("/{orderId}/cod-collected")
    @Transactional
    public ResponseEntity<ResponseMessageUtilityDTO> confirmCodCollection(
            @PathVariable Long orderId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(ResponseMessageUtilityDTO.builder()
                    .status("ERROR").httpStatus(401).message("Authentication is required.").build());
        }
        UserEntity partner = userRepository.findByEmail(authentication.getName());
        if (partner == null || partner.getRole() != Role.DELIVERY_PARTNER) {
            return ResponseEntity.status(403).body(ResponseMessageUtilityDTO.builder()
                    .status("ERROR").httpStatus(403).message("A delivery-partner account is required.").build());
        }
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        if (!CommonConstants.COD.equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalArgumentException("This order is not a COD order.");
        }
        if (order.getCodCollectedAt() != null) {
            return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                    .status(CommonConstants.SUCCESS).httpStatus(200)
                    .message("COD collection was already confirmed.").data(order.getCodCollectedAt()).build());
        }
        if (order.getAssignedDeliveryPartnerId() == null
                || !order.getAssignedDeliveryPartnerId().equals(partner.getId())) {
            return ResponseEntity.status(403).body(ResponseMessageUtilityDTO.builder()
                    .status("ERROR").httpStatus(403).message("This order is not assigned to your delivery account.").build());
        }
        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found."));
        LocalDateTime collectedAt = LocalDateTime.now();
        order.setCodCollectedAt(collectedAt);
        order.setCodCollectedById(partner.getId());
        order.setPaymentStatus(CommonConstants.PAYMENT_STATUS_SUCCESS);
        order.setStatus(NarayanGroup.example.E_Commerce.model.Enum.OrderStatus.DELIVERED);
        payment.setStatus(CommonConstants.PAYMENT_STATUS_SUCCESS);
        paymentRepository.save(payment);
        orderRepository.save(order);
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS).httpStatus(200)
                .message("COD collection confirmed and order marked delivered.")
                .data(collectedAt).build());
    }
}
