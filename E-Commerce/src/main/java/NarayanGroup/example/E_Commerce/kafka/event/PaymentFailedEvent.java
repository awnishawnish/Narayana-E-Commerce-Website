package NarayanGroup.example.E_Commerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private String eventId;
    private Long orderId;
    private String orderNumber;
    private String paymentId;
    private String reason;
    private Long userId;
    private String userEmail;
    private List<PaymentItemEvent> items;
}
