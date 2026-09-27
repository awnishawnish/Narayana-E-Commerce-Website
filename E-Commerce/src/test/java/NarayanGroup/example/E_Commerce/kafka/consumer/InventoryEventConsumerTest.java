package NarayanGroup.example.E_Commerce.kafka.consumer;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentItemEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentFailedEvent;
import NarayanGroup.example.E_Commerce.kafka.idempotency.ProcessedEventRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

class InventoryEventConsumerTest {
    @Test void successfulPayment_shouldFinalizeInventory() {
        IInventoryService inventory = mock(IInventoryService.class);
        ProcessedEventRepository repository = mock(ProcessedEventRepository.class);
        IOrderRepository orderRepository = mock(IOrderRepository.class);
        when(repository.existsByIdEventIdAndIdConsumerName("e1", "inventory-consumer")).thenReturn(false);
        InventoryEventConsumer consumer = new InventoryEventConsumer(inventory, repository,orderRepository);
        PaymentSuccessfulEvent event = PaymentSuccessfulEvent.builder().eventId("e1").orderId(1L)
                .items(Collections.singletonList(PaymentItemEvent.builder().productId(10L).quantity(2L).build())).build();
        consumer.onPaymentSuccessful(event);
        verify(inventory).finalizeReservation(10L, 2L);
        verify(repository).save(any());
    }

    @Test void failedPayment_shouldReleaseInventory() {
        IInventoryService inventory = mock(IInventoryService.class);
        ProcessedEventRepository repository = mock(ProcessedEventRepository.class);
        IOrderRepository orderRepository = mock(IOrderRepository.class);
        when(repository.existsByIdEventIdAndIdConsumerName("e2", "inventory-consumer")).thenReturn(false);
        InventoryEventConsumer consumer = new InventoryEventConsumer(inventory, repository,orderRepository);
        PaymentFailedEvent event = PaymentFailedEvent.builder().eventId("e2").orderId(1L)
                .items(Collections.singletonList(PaymentItemEvent.builder().productId(10L).quantity(2L).build())).build();
        consumer.onPaymentFailed(event);
        verify(inventory).releaseReservation(10L, 2L);
        verify(repository).save(any());
    }

    @Test void duplicateEvent_shouldNotChangeInventory() {
        IInventoryService inventory = mock(IInventoryService.class);
        ProcessedEventRepository repository = mock(ProcessedEventRepository.class);
        IOrderRepository orderRepository = mock(IOrderRepository.class);
        when(repository.existsByIdEventIdAndIdConsumerName("e1", "inventory-consumer")).thenReturn(true);
        InventoryEventConsumer consumer = new InventoryEventConsumer(inventory, repository,orderRepository);
        consumer.onPaymentSuccessful(PaymentSuccessfulEvent.builder().eventId("e1").items(Collections.emptyList()).build());
        verifyNoInteractions(inventory);
        verify(repository, never()).save(any());
    }
}
