package NarayanGroup.example.E_Commerce.DTO.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryResponseDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private boolean custom;
}
