package NarayanGroup.example.E_Commerce.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {

    private Long orderId;

    private String orderNumber;

    private String status;

    private String paymentStatus;

    private BigDecimal totalAmount;

    private String currency;
}
