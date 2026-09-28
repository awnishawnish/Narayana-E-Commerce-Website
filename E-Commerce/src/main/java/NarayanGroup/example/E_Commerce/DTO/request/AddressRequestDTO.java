package NarayanGroup.example.E_Commerce.DTO.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = ErrorConstants.FULL_NAME_REQUIRED)
    private String fullName;

    @NotBlank(message = ErrorConstants.PHONE_REQUIRED)
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = ErrorConstants.PHONE_NUMBER_EXACTLY_10_DIGITS
    )
    private String phone;

    @NotBlank(message = ErrorConstants.ADDRESS_LINE_1_REQUIRED)
    private String addressLine1;

    // Optional
    private String addressLine2;

    @NotBlank(message = ErrorConstants.CITY_REQUIRED)
    private String city;

    @NotBlank(message = ErrorConstants.STATE_REQUIRED)
    private String state;

    @NotBlank(message = ErrorConstants.PINCODE_REQUIRED)
    @Pattern(
            regexp = "^\\d{6}$",
            message = ErrorConstants.PINCODE_EXACTLY_6_DIGITS
    )
    private String pincode;

    // Optional
    private Boolean isDefault;
}
