package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@Validated
public interface IUserController {

    @PostMapping("/saveUserDetails")
    ResponseEntity<ResponseMessageUtilityDTO> saveUserDetails(@Valid @RequestBody UserRequestDTO userRequestDTO);

    @PutMapping("/resetPassword")
    public ResponseEntity<ResponseMessageUtilityDTO> resetPassword(@RequestParam(required=false) String Otp, @Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO);

    @GetMapping("/getAllDetails")
    public ResponseEntity<ResponseMessageUtilityDTO> getAllDetails();

    @PostMapping("/generateOtp")
    public ResponseEntity<ResponseMessageUtilityDTO> generateOtp(@RequestParam String email);

    @GetMapping("/isOtpValid")
    public ResponseEntity<ResponseMessageUtilityDTO> isOtpValid(@RequestParam String email, @RequestParam String otp);

    @GetMapping("/getUserByEmail")
    public ResponseEntity<ResponseMessageUtilityDTO> getUserByEmail(@RequestParam String email);

//    @GetMapping("/google")
//    public ResponseEntity<String> loginSuccess();

    @GetMapping("/login-failure")
    public ResponseEntity<String> loginFailure();

}
