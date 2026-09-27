package NarayanGroup.example.E_Commerce.kafka.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {
    boolean existsByIdEventIdAndIdConsumerName(String eventId, String consumerName);
}
