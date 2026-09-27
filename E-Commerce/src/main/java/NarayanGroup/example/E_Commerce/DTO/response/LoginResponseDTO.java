package NarayanGroup.example.E_Commerce.DTO.response;

import NarayanGroup.example.E_Commerce.model.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    String accessToken;
    String name;
    String email;
    Role role;
}
