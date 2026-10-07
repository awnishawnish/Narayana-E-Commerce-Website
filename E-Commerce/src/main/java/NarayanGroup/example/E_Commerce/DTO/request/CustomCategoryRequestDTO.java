package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomCategoryRequestDTO {

    @NotBlank
    private String name;

    private String description;
}
