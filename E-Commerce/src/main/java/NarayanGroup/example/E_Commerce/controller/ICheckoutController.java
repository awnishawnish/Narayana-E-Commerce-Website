package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public interface ICheckoutController {

    @PostMapping("/{userId}")
    ResponseEntity<ResponseMessageUtilityDTO> checkout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequestDTO request
    );
}