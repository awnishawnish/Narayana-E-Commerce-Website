package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequestDTO {

    private String title;

    private String category;

    @Positive(message = ErrorConstants.PRICE_MUST_BE_POSITIVE)
    private BigDecimal price;

    @Positive(message = ErrorConstants.QUANTITY_MUST_BE_POSITIVE)
    private Integer quantity;

    private String currency;

    private String name;
}
