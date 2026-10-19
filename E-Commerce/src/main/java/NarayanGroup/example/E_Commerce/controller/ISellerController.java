package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public interface ISellerController {
    @PostMapping("/register") ResponseEntity<ResponseMessageUtilityDTO> registerSeller(@Valid @RequestBody SellerRegistrationRequestDTO request, Authentication authentication);
    @GetMapping("/profile") ResponseEntity<ResponseMessageUtilityDTO> getProfile(Authentication authentication);
    @PostMapping("/switch") ResponseEntity<ResponseMessageUtilityDTO> switchToVendor(Authentication authentication, HttpServletResponse response);
    @PostMapping("/switch-to-user") ResponseEntity<ResponseMessageUtilityDTO> switchToUser(Authentication authentication, HttpServletResponse response);
}
