package NarayanGroup.example.E_Commerce.kafka.idempotency;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_event")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @EmbeddedId
    private ProcessedEventId id;

    private LocalDateTime processedAt;

    public ProcessedEvent(String eventId, String consumerName, LocalDateTime processedAt) {
        this.id = new ProcessedEventId(eventId, consumerName);
        this.processedAt = processedAt;
    }
}
