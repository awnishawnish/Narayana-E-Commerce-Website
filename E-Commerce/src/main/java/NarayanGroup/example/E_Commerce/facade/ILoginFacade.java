package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/** Authentication and token lifecycle operations used by the login API. */
public interface ILoginFacade {
    ResponseEntity<ResponseMessageUtilityDTO> authenticateUser(LoginDTO loginDTO, HttpServletResponse response);
    ResponseEntity<?> refreshToken(HttpServletRequest request);
    ResponseEntity<?> logout(HttpServletResponse response);
}
