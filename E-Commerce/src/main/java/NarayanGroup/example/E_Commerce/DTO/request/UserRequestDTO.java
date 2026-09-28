package NarayanGroup.example.E_Commerce.DTO.request;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@Data
@Builder
public class UserRequestDTO {
    @NotBlank(message = ErrorConstants.NAME_REQUIRED)
    private String name;

    @NotBlank(message = ErrorConstants.EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = ErrorConstants.PASSWORD_REQUIRED)
    private String password;
}
