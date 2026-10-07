package NarayanGroup.example.E_Commerce.DTO.request;

import NarayanGroup.example.E_Commerce.model.Enum.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRegistrationRequestDTO {

    @NotBlank
    private String businessName;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @NotNull
    private BusinessType businessType;

    @NotBlank
    private String businessAddress;

    @NotNull
    private Long productCategoryId;
}
