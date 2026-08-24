package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;

public interface IUserFacade {
    public ResponseMessageUtilityDTO saveUserDetails(UserRequestDTO userRequestDTO);

    public ResponseMessageUtilityDTO resetPassword(String otp, ResetPasswordRequestDTO resetPasswordRequestDTO);

    public ResponseMessageUtilityDTO isOtpValid(String email, String otp);

    public ResponseMessageUtilityDTO generateOtp(String email);

    public ResponseMessageUtilityDTO getUserByEmail(String email);

    public ResponseMessageUtilityDTO getAllDetails();
    public UserEntity getByEmailAndPassword(String email, String password);
}
