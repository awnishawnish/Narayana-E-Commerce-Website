package NarayanGroup.example.E_Commerce.kafka.consumer;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventRepository;
import NarayanGroup.example.E_Commerce.notification.NotificationService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NotificationEventConsumerTest {
    @Test void successfulPayment_shouldSendEmailAndMarkProcessed() {
        NotificationService service = mock(NotificationService.class);
        ProcessedEventRepository repository = mock(ProcessedEventRepository.class);
        when(repository.existsByIdEventIdAndIdConsumerName("e1", "notification-consumer")).thenReturn(false);
        NotificationEventConsumer consumer = new NotificationEventConsumer(service, repository);
        consumer.onPaymentSuccessful(PaymentSuccessfulEvent.builder().eventId("e1").build());
        verify(service).sendPaymentSuccessEmail(any());
        verify(repository).save(any());
    }
}
