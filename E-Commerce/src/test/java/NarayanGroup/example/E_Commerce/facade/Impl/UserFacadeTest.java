package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.IUserService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFacadeTest {
    private final IUserService service=mock(IUserService.class);
    private final UserFacade facade=new UserFacade(service);

    @Test void saveUserDetails_shouldBuildCreatedResponse() {
        var request=UserRequestDTO.builder().email("u@test.com").build(); var user=UserEntity.builder().id(1L).build();
        when(service.saveUserDetails(request)).thenReturn(user);
        var r=facade.saveUserDetails(request);
        assertEquals(CommonConstants.SUCCESS,r.getStatus()); assertEquals(201,r.getHttpStatus()); assertSame(user,r.getData());
    }
    @Test void resetPassword_shouldDelegate() {
        var request=ResetPasswordRequestDTO.builder().email("u@test.com").password("new").build();
        var r=facade.resetPassword("123456",request);
        assertEquals(CommonConstants.PASSWORD_RESET_SUCCESSFULLY,r.getMessage());
        verify(service).resetPassword("123456","u@test.com","new");
    }
    @Test void generateOtp_shouldReturnSuccessAndFailure() {
        when(service.generateOtp("u@test.com")).thenReturn(true);
        var success=facade.generateOtp("u@test.com");
        assertEquals(200,success.getHttpStatus()); assertEquals(CommonConstants.OTP_SENT_SUCCESSFULLY,success.getMessage()); assertEquals(true,success.getData());

        when(service.generateOtp("bad@test.com")).thenReturn(false);
        var failure=facade.generateOtp("bad@test.com");
        assertEquals(400,failure.getHttpStatus()); assertEquals(ErrorConstants.FAILED_TO_SEND_OTP,failure.getMessage());
    }
    @Test void isOtpValid_shouldReturnSuccessAndFailure() {
        when(service.isOtpValid("u@test.com","123456")).thenReturn(true);
        assertEquals(200,facade.isOtpValid("u@test.com","123456").getHttpStatus());
        when(service.isOtpValid("u@test.com","bad")).thenReturn(false);
        assertEquals(ErrorConstants.INVALID_OR_EXPIRED_OTP,facade.isOtpValid("u@test.com","bad").getMessage());
    }
    @Test void getUserByEmail_shouldDelegate() {
        var user=UserEntity.builder().id(1L).build(); when(service.getUserByEmail("u@test.com")).thenReturn(user);
        assertSame(user,facade.getUserByEmail("u@test.com").getData());
    }
    @Test void getAllDetails_shouldDelegate() {
        var users=List.of(UserEntity.builder().id(1L).build()); when(service.getAllDetails()).thenReturn(users);
        assertSame(users,facade.getAllDetails().getData());
    }
    @Test void getByEmailAndPassword_shouldDelegate() {
        var user=UserEntity.builder().id(1L).build(); when(service.getByEmailAndPassword("u","p")).thenReturn(user);
        assertSame(user,facade.getByEmailAndPassword("u","p"));
    }
}
