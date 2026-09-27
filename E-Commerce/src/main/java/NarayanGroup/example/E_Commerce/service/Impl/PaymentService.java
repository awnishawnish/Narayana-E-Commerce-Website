package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentItemEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentFailedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.service.IPaymentService;
import NarayanGroup.example.E_Commerce.service.ICartService;
import NarayanGroup.example.E_Commerce.service.IRazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final DomainEventProducer eventProducer;
    private final IRazorpayService razorpayService;
    private final ICartService cartService;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public Payment verifyRazorpayPayment(Long userId, PaymentVerificationRequestDTO request) {
        Order order = orderRepository.findByIdAndUserIdAndIsDeletedFalse(request.getOrderId(), userId)
                .orElseThrow(() -> new CustomException.CheckoutException("Order not found"));

        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(order.getId())
                .orElseThrow(() -> new CustomException.CheckoutException("Payment not found"));

        if (!"RAZORPAY".equalsIgnoreCase(payment.getPaymentMethod())) {
            throw new CustomException.CheckoutException("Order is not a Razorpay payment");
        }

        if (!request.getRazorpayOrderId().equals(payment.getGatewayOrderId())) {
            throw new CustomException.CheckoutException("Razorpay order does not match our payment");
        }

        if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            return payment;
        }

        if (!isValidSignature(payment.getGatewayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            throw new CustomException.CheckoutException("Invalid Razorpay payment signature");
        }

        try {
            boolean captured = razorpayService.isPaymentCaptured(
                    request.getRazorpayPaymentId(),
                    payment.getGatewayOrderId(),
                    payment.getAmount(),
                    payment.getCurrency());
            if (!captured) {
                throw new CustomException.CheckoutException("Razorpay payment is not captured or does not match the order");
            }
        } catch (CustomException.CheckoutException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException.CheckoutException("Unable to validate Razorpay payment");
        }

        payment.setGatewayPaymentId(request.getRazorpayPaymentId());
        payment.setGatewaySignature(request.getRazorpaySignature());
        payment.setStatus("SUCCESS");
        Payment savedPayment = paymentRepository.save(payment);

        order.setPaymentStatus("SUCCESS");
        order.setStatus(NarayanGroup.example.E_Commerce.model.Enum.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        PaymentSuccessfulEvent event = PaymentSuccessfulEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentId(payment.getPaymentId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(order.getOrderItems().stream()
                        .map(item -> PaymentItemEvent.builder()
                                .productId(item.getProduct().getId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        publishAfterCommit(() -> {
            eventProducer.publishPaymentSuccessful(event);
            order.getOrderItems().forEach(item -> {
                if (item.getSourceCartItemId() != null) {
                    try {
                        cartService.removeItem(order.getUser().getId(), item.getSourceCartItemId());
                    } catch (Exception ignored) {
                        // Payment is already verified. A later cart refresh/reconciliation can clean stale items.
                    }
                }
            });
        });
        return savedPayment;
    }

    @Override
    @Transactional
    public Payment markRazorpayPaymentFailed(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndUserIdAndIsDeletedFalse(orderId, userId)
                .orElseThrow(() -> new CustomException.CheckoutException("Order not found"));
        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException.CheckoutException("Payment not found"));

        if (!"RAZORPAY".equalsIgnoreCase(payment.getPaymentMethod())) {
            throw new CustomException.CheckoutException("Order is not a Razorpay payment");
        }
        if ("SUCCESS".equalsIgnoreCase(payment.getStatus())
                || "CANCELLED".equalsIgnoreCase(payment.getStatus())
                || "REFUNDED".equalsIgnoreCase(payment.getStatus())) {
            return payment;
        }

        payment.setStatus("FAILED");
        Payment saved = paymentRepository.save(payment);
        order.setPaymentStatus("FAILED");
        order.setStatus(NarayanGroup.example.E_Commerce.model.Enum.OrderStatus.CANCELLED);
        orderRepository.save(order);

        PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentId(payment.getPaymentId())
                .reason(reason == null || reason.trim().isEmpty() ? "Payment failed" : reason)
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(order.getOrderItems().stream()
                        .map(item -> PaymentItemEvent.builder()
                                .productId(item.getProduct().getId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        publishAfterCommit(() -> eventProducer.publishPaymentFailed(failedEvent));
        return saved;
    }

    private void publishAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    private boolean isValidSignature(String orderId, String paymentId, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
            return constantTimeEquals(toHex(digest), signature);
        } catch (Exception ex) {
            throw new CustomException.CheckoutException("Unable to verify Razorpay payment");
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private boolean constantTimeEquals(String expected, String actual) {
        byte[] a = expected.getBytes(StandardCharsets.UTF_8);
        byte[] b = actual.getBytes(StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(a, b);
    }
}
