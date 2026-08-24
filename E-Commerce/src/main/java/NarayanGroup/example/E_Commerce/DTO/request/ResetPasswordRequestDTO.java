package NarayanGroup.example.E_Commerce.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequestDTO {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
}
