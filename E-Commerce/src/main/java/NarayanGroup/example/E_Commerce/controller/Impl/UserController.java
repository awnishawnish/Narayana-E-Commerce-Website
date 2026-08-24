package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IUserController;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController implements IUserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserFacade iUserFacade;

    public ResponseEntity<ResponseMessageUtilityDTO> saveUserDetails(UserRequestDTO userRequestDTO) {
        logger.info("Received request to save user details for email: {}", userRequestDTO.getEmail());
        ResponseMessageUtilityDTO response = iUserFacade.saveUserDetails(userRequestDTO);
        logger.debug("Response from saveUserDetails: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

    public ResponseEntity<ResponseMessageUtilityDTO> resetPassword(String  otp, ResetPasswordRequestDTO resetPasswordRequestDTO) {
        logger.info("Received request to reset password for email: {}", resetPasswordRequestDTO.getEmail());
        ResponseMessageUtilityDTO response = iUserFacade.resetPassword(otp,resetPasswordRequestDTO);
        logger.debug("Response from ResetPassword: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

    public ResponseEntity<ResponseMessageUtilityDTO> getUserByEmail(String email) {
        logger.info("Fetching user details for email: {}", email);
        ResponseMessageUtilityDTO response = iUserFacade.getUserByEmail(email);
        logger.debug("Response from getUserByEmail: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

    public ResponseEntity<ResponseMessageUtilityDTO> getAllDetails() {
        logger.info("Fetching all user details");
        ResponseMessageUtilityDTO response = iUserFacade.getAllDetails();
        logger.debug("Response from getAllDetails: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

    public ResponseEntity<ResponseMessageUtilityDTO> generateOtp(String email) {
        logger.info("Generating OTP for email: {}", email);
        ResponseMessageUtilityDTO response = iUserFacade.generateOtp(email);
        logger.debug("Response from generateOtp: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

    public ResponseEntity<ResponseMessageUtilityDTO> isOtpValid(String email, String otp) {
        logger.info("Validating OTP for email: {} with otp: {}", email, otp);
        ResponseMessageUtilityDTO response = iUserFacade.isOtpValid(email, otp);
        logger.debug("Response from isOtpValid: {}", response);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatus()));
    }

//    public ResponseEntity<String> loginSuccess() {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        logger.info("Login attempt detected: {}", auth);
//
//        if (!(auth instanceof OAuth2AuthenticationToken)) {
//            logger.warn("Unauthorized login attempt - not OAuth2");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("User is not authenticated via OAuth2");
//        }
//
//        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) auth;
//        String email = authentication.getPrincipal().getAttribute("email");
//        String name = authentication.getPrincipal().getAttribute("name");
//
//        logger.info("Login successful for user: {} ({})", name, email);
//        return ResponseEntity.ok("Login successful! User: " + name + " (" + email + ")");
//    }




    public ResponseEntity<String> loginFailure() {
        logger.error("Login failed for user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed!");
    }
}
