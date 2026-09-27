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
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productTitle;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String currency;
}
