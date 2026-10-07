package NarayanGroup.example.E_Commerce.DTO.response;

import NarayanGroup.example.E_Commerce.model.Enum.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private String accessToken;
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Role activeRole;
    private boolean seller;
}
