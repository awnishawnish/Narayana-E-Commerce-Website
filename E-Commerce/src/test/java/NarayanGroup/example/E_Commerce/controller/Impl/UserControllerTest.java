package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private final IUserFacade facade=mock(IUserFacade.class);
    private final UserController controller=new UserController(facade);

    private ResponseMessageUtilityDTO body(int status) { return ResponseMessageUtilityDTO.builder().httpStatus(status).build(); }

    @Test void saveUserDetails_shouldUseFacadeStatus() {
        var request=UserRequestDTO.builder().email("u@test.com").build(); var body=body(201);
        when(facade.saveUserDetails(request)).thenReturn(body);
        var response=controller.saveUserDetails(request);
        assertEquals(HttpStatus.CREATED,response.getStatusCode()); assertSame(body,response.getBody());
    }
    @Test void resetPassword_shouldUseFacadeStatus() {
        var request=ResetPasswordRequestDTO.builder().email("u@test.com").password("new").build(); var body=body(200);
        when(facade.resetPassword("123456",request)).thenReturn(body);
        assertEquals(HttpStatus.OK,controller.resetPassword("123456",request).getStatusCode());
    }
    @Test void getUserByEmail_shouldUseFacadeStatus() {
        var body=body(200); when(facade.getUserByEmail("u@test.com")).thenReturn(body);
        assertEquals(HttpStatus.OK,controller.getUserByEmail("u@test.com").getStatusCode());
    }
    @Test void getAllDetails_shouldUseFacadeStatus() {
        var body=body(200); when(facade.getAllDetails()).thenReturn(body);
        assertEquals(HttpStatus.OK,controller.getAllDetails().getStatusCode());
    }
    @Test void generateOtp_shouldUseFacadeStatus() {
        var body=body(200); when(facade.generateOtp("u@test.com")).thenReturn(body);
        assertEquals(HttpStatus.OK,controller.generateOtp("u@test.com").getStatusCode());
    }
    @Test void isOtpValid_shouldUseFacadeStatus() {
        var body=body(200); when(facade.isOtpValid("u@test.com","123456")).thenReturn(body);
        assertEquals(HttpStatus.OK,controller.isOtpValid("u@test.com","123456").getStatusCode());
    }
    @Test void loginFailure_shouldReturnUnauthorized() {
        var response=controller.loginFailure();
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
        assertEquals(ErrorConstants.LOGIN_FAILED,response.getBody());
    }
}
