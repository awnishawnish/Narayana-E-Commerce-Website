package NarayanGroup.example.E_Commerce.kafka.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProcessedEventId implements Serializable {

    @Column(name = "event_id", nullable = false, length = 100)
    private String eventId;

    @Column(name = "consumer_name", nullable = false, length = 100)
    private String consumerName;
}
