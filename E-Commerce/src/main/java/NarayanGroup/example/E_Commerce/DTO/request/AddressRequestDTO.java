package NarayanGroup.example.E_Commerce.DTO.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    // Optional
    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Pincode must contain exactly 6 digits"
    )
    private String pincode;

    // Optional
    private Boolean isDefault;
}
