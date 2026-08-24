package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.IUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserFacade implements IUserFacade {

    private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);
    private final IUserService iUserService;

    public ResponseMessageUtilityDTO saveUserDetails(UserRequestDTO userRequestDTO) {
        logger.info("Saving user details for email: {}", userRequestDTO.getEmail());
        UserEntity userEntity = iUserService.saveUserDetails(userRequestDTO);
        logger.debug("User saved: {}", userEntity);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(201)
                .message("User registered successfully")
                .data(userEntity)
                .build();
    }

    public ResponseMessageUtilityDTO resetPassword(String otp, ResetPasswordRequestDTO resetPasswordRequestDTO) {
        logger.info("Resetting password for email: {}", resetPasswordRequestDTO.getEmail());
        iUserService.resetPassword(otp,resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getPassword());
        logger.debug("Password reset completed for email: {}", resetPasswordRequestDTO.getEmail());
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Password reset successfully")
                .build();
    }

    public ResponseMessageUtilityDTO generateOtp(String email) {
        logger.info("Generating OTP for email: {}", email);
        boolean response = iUserService.generateOtp(email);
        if (!response) {
            logger.warn("Failed to generate OTP for email: {}", email);
            return ResponseMessageUtilityDTO.builder()
                    .status("Failure")
                    .httpStatus(400)
                    .message("Failed to send OTP. Email may not be registered.")
                    .data(response)
                    .build();
        }
        logger.info("OTP generated successfully for email: {}", email);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("OTP sent successfully")
                .data(response)
                .build();
    }

    public ResponseMessageUtilityDTO isOtpValid(String email, String otp) {
        logger.info("Validating OTP for email: {}", email);
        Boolean response = iUserService.isOtpValid(email, otp);
        if (!response) {
            logger.warn("Invalid or expired OTP for email: {}", email);
            return ResponseMessageUtilityDTO.builder()
                    .status("Failure")
                    .httpStatus(400)
                    .message("Invalid or expired OTP")
                    .build();
        }
        logger.info("OTP validated successfully for email: {}", email);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("OTP validation status")
                .data(response)
                .build();
    }

    public ResponseMessageUtilityDTO getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        UserEntity userEntity = iUserService.getUserByEmail(email);
        logger.debug("Fetched user: {}", userEntity);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("User fetched successfully")
                .data(userEntity)
                .build();
    }

    public ResponseMessageUtilityDTO getAllDetails() {
        logger.info("Fetching all user details");
        List<UserEntity> users = iUserService.getAllDetails();
        logger.debug("Fetched {} users", users.size());
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("All user details fetched successfully")
                .data(users)
                .build();
    }
   public UserEntity getByEmailAndPassword(String email, String password){

      return iUserService.getByEmailAndPassword(email, password);
   }
}
