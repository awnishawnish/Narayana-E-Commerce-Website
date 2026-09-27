package NarayanGroup.example.E_Commerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent {
    private String eventId;
    private Long orderId;
    private String orderNumber;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;
    private String currency;
    private Long userId;
    private String userEmail;
    private String reason;
    private List<PaymentItemEvent> items;
}
