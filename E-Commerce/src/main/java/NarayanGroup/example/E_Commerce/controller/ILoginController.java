package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public interface ILoginController {
    @PostMapping("/login") ResponseEntity<ResponseMessageUtilityDTO> authenticateUser(@RequestBody LoginDTO loginDTO, HttpServletResponse response);
    @PostMapping("/refresh-token") ResponseEntity<?> refreshToken(HttpServletRequest request);
    @PostMapping("/logout") ResponseEntity<?> logout(HttpServletResponse response);
}
