package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;

@Data
@AllArgsConstructor
public class AddCartItemRequestDTO {
    @NotNull(message = ErrorConstants.PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = ErrorConstants.QUANTITY_REQUIRED)
    @Positive(message = ErrorConstants.QUANTITY_GREATER_THAN_ZERO)
    private Integer quantity;

}
