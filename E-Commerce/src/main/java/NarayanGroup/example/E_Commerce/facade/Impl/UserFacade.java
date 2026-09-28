package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.IUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
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
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.USER_REGISTERED_SUCCESSFULLY)
                .data(userEntity)
                .build();
    }

    public ResponseMessageUtilityDTO resetPassword(String otp, ResetPasswordRequestDTO resetPasswordRequestDTO) {
        logger.info("Resetting password for email: {}", resetPasswordRequestDTO.getEmail());
        iUserService.resetPassword(otp,resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getPassword());
        logger.debug("Password reset completed for email: {}", resetPasswordRequestDTO.getEmail());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PASSWORD_RESET_SUCCESSFULLY)
                .build();
    }

    public ResponseMessageUtilityDTO generateOtp(String email) {
        logger.info("Generating OTP for email: {}", email);
        boolean response = iUserService.generateOtp(email);
        if (!response) {
            logger.warn("Failed to generate OTP for email: {}", email);
            return ResponseMessageUtilityDTO.builder()
                    .status(CommonConstants.FAILURE)
                    .httpStatus(400)
                    .message(ErrorConstants.FAILED_TO_SEND_OTP)
                    .data(response)
                    .build();
        }
        logger.info("OTP generated successfully for email: {}", email);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.OTP_SENT_SUCCESSFULLY)
                .data(response)
                .build();
    }

    public ResponseMessageUtilityDTO isOtpValid(String email, String otp) {
        logger.info("Validating OTP for email: {}", email);
        Boolean response = iUserService.isOtpValid(email, otp);
        if (!response) {
            logger.warn("Invalid or expired OTP for email: {}", email);
            return ResponseMessageUtilityDTO.builder()
                    .status(CommonConstants.FAILURE)
                    .httpStatus(400)
                    .message(ErrorConstants.INVALID_OR_EXPIRED_OTP)
                    .build();
        }
        logger.info("OTP validated successfully for email: {}", email);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.OTP_VALIDATION_STATUS)
                .data(response)
                .build();
    }

    public ResponseMessageUtilityDTO getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        UserEntity userEntity = iUserService.getUserByEmail(email);
        logger.debug("Fetched user: {}", userEntity);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.USER_FETCHED_SUCCESSFULLY)
                .data(userEntity)
                .build();
    }

    public ResponseMessageUtilityDTO getAllDetails() {
        logger.info("Fetching all user details");
        List<UserEntity> users = iUserService.getAllDetails();
        logger.debug("Fetched {} users", users.size());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.ALL_USER_DETAILS_FETCHED_SUCCESSFULLY)
                .data(users)
                .build();
    }
   public UserEntity getByEmailAndPassword(String email, String password){

      return iUserService.getByEmailAndPassword(email, password);
   }
}
