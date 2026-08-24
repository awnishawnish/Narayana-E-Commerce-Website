package NarayanGroup.example.E_Commerce.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// ✅ NEW FILE: ProductResponseDTO
// Reason: Hides `isDeleted` and other internal fields from API consumers.
// Deleted products are filtered at service level, so clients never see them.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String title;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String currency;
    private String imageKey; // S3 URL for the product image
}
