package NarayanGroup.example.E_Commerce.kafka.consumer;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventId;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventRepository;
import NarayanGroup.example.E_Commerce.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {
    private static final String CONSUMER = "notification-consumer";
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "${app.kafka.topics.payment-successful:payment-successful}",
            groupId = "notification-service",
            containerFactory = "paymentSuccessfulKafkaListenerContainerFactory")
    @Transactional
    public void onPaymentSuccessful(PaymentSuccessfulEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        notificationService.sendPaymentSuccessEmail(event);
        processedEventRepository.save(ProcessedEvent.builder()
                .id(new ProcessedEventId(event.getEventId(), CONSUMER))
                .processedAt(LocalDateTime.now()).build());
    }

    @KafkaListener(topics = "${app.kafka.topics.order-confirmed:order-confirmed}",
            groupId = "notification-service",
            containerFactory = "orderConfirmedKafkaListenerContainerFactory")
    @Transactional
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        notificationService.sendOrderConfirmationEmail(event);
        processedEventRepository.save(ProcessedEvent.builder()
                .id(new ProcessedEventId(event.getEventId(), CONSUMER))
                .processedAt(LocalDateTime.now()).build());
    }
    @KafkaListener(topics = "${app.kafka.topics.order-cancelled:order-cancelled}",
            groupId = "notification-service",
            containerFactory = "orderCancelledKafkaListenerContainerFactory")
    @Transactional
    public void onOrderCancelled(OrderCancelledEvent event) {
        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(event.getEventId(), CONSUMER)) return;
        notificationService.sendOrderCancellationEmail(event);
        processedEventRepository.save(ProcessedEvent.builder()
                .id(new ProcessedEventId(event.getEventId(), CONSUMER))
                .processedAt(LocalDateTime.now()).build());
    }

}
