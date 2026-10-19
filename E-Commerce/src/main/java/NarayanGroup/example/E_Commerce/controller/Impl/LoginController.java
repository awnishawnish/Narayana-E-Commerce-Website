package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.ILoginController;
import NarayanGroup.example.E_Commerce.facade.ILoginFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController implements ILoginController {
    private final ILoginFacade loginFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> authenticateUser(LoginDTO loginDTO, HttpServletResponse response) {
        return loginFacade.authenticateUser(loginDTO, response);
    }

    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return loginFacade.refreshToken(request);
    }

    @Override
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return loginFacade.logout(response);
    }
}
