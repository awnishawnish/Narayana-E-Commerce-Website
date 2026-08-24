package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequestDTO {

    private String title;

    private String category;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private String currency;

    private String name;
}
