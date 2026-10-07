package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSubcategoryRequestDTO {

    @NotBlank
    private String name;

    private String description;
}
