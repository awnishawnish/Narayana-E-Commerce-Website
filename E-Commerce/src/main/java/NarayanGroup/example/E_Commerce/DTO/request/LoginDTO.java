package NarayanGroup.example.E_Commerce.DTO.request;

import lombok.*;


@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String email;
    private String password;

}
