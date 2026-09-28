package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.OrderItemResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.OrderResponseDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentItemEvent;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.IOrderService;
import NarayanGroup.example.E_Commerce.service.IRazorpayService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IRazorpayService razorpayService;
    private final DomainEventProducer eventProducer;

    @Value("${app.checkout.payment-timeout-minutes:15}")
    private long paymentTimeoutMinutes;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrders(Long userId) {
        return orderRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrder(Long userId, Long orderId) {
        return toDTO(findOrder(userId, orderId));
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        Order order = findOrder(userId, orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) return;
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new CustomException.CheckoutException(ErrorConstants.ORDER_CANNOT_BE_CANCELLED);
        }

        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException.CheckoutException(ErrorConstants.PAYMENT_NOT_FOUND));
        boolean paid = CommonConstants.PAYMENT_STATUS_SUCCESS.equalsIgnoreCase(payment.getStatus());

        if (paid && CommonConstants.RAZORPAY.equalsIgnoreCase(payment.getPaymentMethod())) {
            try {
                razorpayService.refundPayment(payment.getGatewayPaymentId(), payment.getAmount(), payment.getCurrency());
            } catch (Exception ex) {
                throw new CustomException.CheckoutException(ErrorConstants.REFUND_FAILED);
            }
            payment.setStatus(CommonConstants.PAYMENT_STATUS_REFUNDED);
            order.setPaymentStatus(CommonConstants.PAYMENT_STATUS_REFUNDED);
        } else if (!paid) {
            payment.setStatus("CANCELLED");
            order.setPaymentStatus("CANCELLED");
        } else {
            payment.setStatus(CommonConstants.PAYMENT_STATUS_REFUNDED);
            order.setPaymentStatus(CommonConstants.PAYMENT_STATUS_REFUNDED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        paymentRepository.save(payment);
        orderRepository.save(order);

        String cancellationReason = reason == null || reason.trim().isEmpty() ? "Cancelled by customer" : reason.trim();
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .amount(order.getTotalAmount())
                .currency(order.getCurrency())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .reason(cancellationReason)
                .items(order.getOrderItems().stream()
                        .map(item -> PaymentItemEvent.builder()
                                .productId(item.getProduct().getId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        publishAfterCommit(() -> eventProducer.publishOrderCancelled(event));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoice(Long userId, Long orderId) {
        Order order = findOrder(userId, orderId);
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            float y = 750;

            write(content, bold, 20, 50, y, "BAZAR"); y -= 28;
            write(content, bold, 14, 50, y, "INVOICE"); y -= 25;
            write(content, regular, 10, 50, y, "Invoice No: INV-" + order.getOrderNumber());
            write(content, regular, 10, 350, y, "Order No: " + order.getOrderNumber()); y -= 15;
            write(content, regular, 10, 50, y, "Order Date: " + order.getCreatedAt()); y -= 30;

            write(content, bold, 11, 50, y, "Customer"); y -= 15;
            write(content, regular, 10, 50, y, order.getShippingFullName()); y -= 13;
            write(content, regular, 10, 50, y, order.getShippingPhone()); y -= 13;
            write(content, regular, 10, 50, y, order.getUser().getEmail()); y -= 13;
            write(content, regular, 10, 50, y, order.getShippingAddressLine1());
            if (order.getShippingAddressLine2() != null && !order.getShippingAddressLine2().trim().isEmpty()) { y -= 13; write(content, regular, 10, 50, y, order.getShippingAddressLine2()); }
            y -= 13;
            write(content, regular, 10, 50, y, order.getShippingCity() + ", " + order.getShippingState() + " - " + order.getShippingPincode()); y -= 30;

            write(content, bold, 10, 50, y, "Product");
            write(content, bold, 10, 300, y, "Qty");
            write(content, bold, 10, 360, y, "Unit Price");
            write(content, bold, 10, 460, y, "Total"); y -= 15;
            for (var item : order.getOrderItems()) {
                write(content, regular, 9, 50, y, truncate(item.getProductTitle(), 38));
                write(content, regular, 9, 300, y, String.valueOf(item.getQuantity()));
                write(content, regular, 9, 360, y, money(item.getUnitPrice(), order.getCurrency()));
                write(content, regular, 9, 460, y, money(item.getTotalPrice(), order.getCurrency())); y -= 14;
            }

            y -= 15;
            write(content, regular, 10, 360, y, "Subtotal"); write(content, regular, 10, 460, y, money(order.getSubtotal(), order.getCurrency())); y -= 15;
            write(content, regular, 10, 360, y, "Delivery"); write(content, regular, 10, 460, y, money(order.getDeliveryCharge(), order.getCurrency())); y -= 15;
            write(content, regular, 10, 360, y, "Discount"); write(content, regular, 10, 460, y, money(order.getDiscount(), order.getCurrency())); y -= 18;
            write(content, bold, 12, 360, y, "Grand Total"); write(content, bold, 12, 460, y, money(order.getTotalAmount(), order.getCurrency())); y -= 25;
            write(content, regular, 10, 50, y, "Payment Method: " + order.getPaymentMethod());
            write(content, regular, 10, 300, y, "Payment Status: " + order.getPaymentStatus()); y -= 30;
            write(content, regular, 9, 50, y, "Order Status: " + order.getStatus()); y -= 25;
            write(content, regular, 9, 50, y, "Thank you for shopping with BAZAR.");

            content.close();
            document.save(output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new CustomException.CheckoutException(ErrorConstants.INVOICE_GENERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void expirePendingPayment(Long orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.PENDING_PAYMENT) return;
        if (order.getCreatedAt() == null || order.getCreatedAt().plusMinutes(paymentTimeoutMinutes).isAfter(LocalDateTime.now())) return;
        cancelOrder(order.getUser().getId(), order.getId(), ErrorConstants.PAYMENT_WINDOW_EXPIRED);
    }

    private Order findOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserIdAndIsDeletedFalse(orderId, userId)
                .orElseThrow(() -> new CustomException.OrderNotFoundException(ErrorConstants.ORDER_NOT_FOUND));
    }

    private OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId()).orderNumber(order.getOrderNumber()).status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus()).paymentMethod(order.getPaymentMethod())
                .subtotal(order.getSubtotal()).tax(order.getTax()).deliveryCharge(order.getDeliveryCharge())
                .discount(order.getDiscount()).totalAmount(order.getTotalAmount()).currency(order.getCurrency())
                .shippingFullName(order.getShippingFullName()).shippingPhone(order.getShippingPhone())
                .shippingAddressLine1(order.getShippingAddressLine1()).shippingAddressLine2(order.getShippingAddressLine2())
                .shippingCity(order.getShippingCity()).shippingState(order.getShippingState()).shippingPincode(order.getShippingPincode())
                .createdAt(order.getCreatedAt()).updatedAt(order.getUpdatedAt())
                .items(order.getOrderItems().stream().map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId()).productId(item.getProduct().getId()).productTitle(item.getProductTitle())
                        .quantity(item.getQuantity()).unitPrice(item.getUnitPrice()).totalPrice(item.getTotalPrice()).currency(item.getCurrency()).build())
                        .collect(Collectors.toList()))
                .build();
    }

    private void publishAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { action.run(); }
            });
        } else action.run();
    }

    private void write(PDPageContentStream content, PDType1Font font, float size, float x, float y, String text) throws Exception {
        content.beginText(); content.setFont(font, size); content.newLineAtOffset(x, y); content.showText(safe(text)); content.endText();
    }

    private String safe(String value) { return value == null ? "" : value.replaceAll("[^\\x20-\\x7E]", "?"); }
    private String truncate(String value, int max) { String s = safe(value); return s.length() <= max ? s : s.substring(0, max - 3) + "..."; }
    private String money(BigDecimal amount, String currency) { return (currency == null ? "" : currency + " ") + (amount == null ? "0.00" : amount.toPlainString()); }
}
