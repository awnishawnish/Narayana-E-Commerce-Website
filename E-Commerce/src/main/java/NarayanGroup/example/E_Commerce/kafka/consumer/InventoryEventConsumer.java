package NarayanGroup.example.E_Commerce.kafka.consumer;

import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentFailedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventId;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventRepository;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private static final String CONSUMER = "inventory-consumer";
    private final IInventoryService inventoryService;
    private final ProcessedEventRepository processedEventRepository;
    private final IOrderRepository orderRepository;

    @KafkaListener(topics = "${app.kafka.topics.payment-successful:payment-successful}",
            groupId = "inventory-service",
            containerFactory = "paymentSuccessfulKafkaListenerContainerFactory")
    @Transactional
    public void onPaymentSuccessful(PaymentSuccessfulEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.getOrderId()).orElse(null);
        if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
            event.getItems().forEach(item -> inventoryService.finalizeReservation(item.getProductId(), item.getQuantity()));
        }
        markProcessed(event.getEventId());
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-failed:payment-failed}",
            groupId = "inventory-service",
            containerFactory = "paymentFailedKafkaListenerContainerFactory")
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        event.getItems().forEach(item -> inventoryService.releaseReservation(item.getProductId(), item.getQuantity()));
        markProcessed(event.getEventId());
    }

    @KafkaListener(topics = "${app.kafka.topics.order-confirmed:order-confirmed}",
            groupId = "inventory-service",
            containerFactory = "orderConfirmedKafkaListenerContainerFactory")
    @Transactional
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.getOrderId()).orElse(null);
        if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
            event.getItems().forEach(item -> inventoryService.finalizeReservation(item.getProductId(), item.getQuantity()));
        }
        markProcessed(event.getEventId());
    }

    @KafkaListener(topics = "${app.kafka.topics.order-cancelled:order-cancelled}",
            groupId = "inventory-service",
            containerFactory = "orderCancelledKafkaListenerContainerFactory")
    @Transactional
    public void onOrderCancelled(OrderCancelledEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        event.getItems().forEach(item -> inventoryService.cancelReservationOrRestoreStock(item.getProductId(), item.getQuantity()));
        markProcessed(event.getEventId());
    }

    private void markProcessed(String eventId) {
        processedEventRepository.save(ProcessedEvent.builder()
                .id(new ProcessedEventId(eventId, CONSUMER))
                .processedAt(LocalDateTime.now()).build());
    }
}
