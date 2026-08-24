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
public class CartItemResponseDTO {

    private Long cartItemId;

    private ProductSummaryDTO product;

    private Integer quantity;

    private BigDecimal priceAtAddition;

    private String currency;

}