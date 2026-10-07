package NarayanGroup.example.E_Commerce.DTO.response;

import NarayanGroup.example.E_Commerce.model.Enum.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveRoleResponseDTO {
    private String accessToken;
    private Role activeRole;
    private boolean seller;
}
