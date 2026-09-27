package NarayanGroup.example.E_Commerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemEvent {
    private Long productId;
    private Long quantity;
}
