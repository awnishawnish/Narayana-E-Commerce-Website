package NarayanGroup.example.E_Commerce.kafka.idempotency;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProcessedEventTest {
    @Test
    void sameEventCanBeProcessedByDifferentConsumers() {
        ProcessedEventId inventory = new ProcessedEventId("event-1", "inventory-consumer");
        ProcessedEventId notification = new ProcessedEventId("event-1", "notification-consumer");

        assertNotEquals(inventory, notification);
        assertEquals(inventory, new ProcessedEventId("event-1", "inventory-consumer"));
    }

    @Test
    void constructor_shouldCreateCompositeId() {
        LocalDateTime now = LocalDateTime.now();
        ProcessedEvent event = new ProcessedEvent("event-1", "inventory-consumer", now);

        assertEquals("event-1", event.getId().getEventId());
        assertEquals("inventory-consumer", event.getId().getConsumerName());
        assertEquals(now, event.getProcessedAt());
    }
}
