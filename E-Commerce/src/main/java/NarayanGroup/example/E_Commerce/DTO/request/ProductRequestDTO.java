package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = ErrorConstants.TITLE_REQUIRED)
    private String title;

    @NotBlank(message = ErrorConstants.CATEGORY_REQUIRED)
    private String category;

    @NotNull(message = ErrorConstants.PRICE_REQUIRED)
    @Positive(message = ErrorConstants.PRICE_MUST_BE_POSITIVE)
    private BigDecimal price;

    @NotNull(message = ErrorConstants.QUANTITY_REQUIRED)
    @Positive(message = ErrorConstants.QUANTITY_MUST_BE_POSITIVE)
    private Integer quantity;

    private String currency;

    private String name;

    private String description;

    private String imageName;

}
