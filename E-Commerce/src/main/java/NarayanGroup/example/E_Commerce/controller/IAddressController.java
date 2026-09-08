package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public interface IAddressController {

    @PostMapping("/{userId}")
    ResponseEntity<ResponseMessageUtilityDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequestDTO request
    );

    @GetMapping("/{userId}")
    ResponseEntity<ResponseMessageUtilityDTO> getAddresses(
            @PathVariable Long userId
    );

    @PutMapping("/{userId}/{addressId}")
    ResponseEntity<ResponseMessageUtilityDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO request
    );

    @DeleteMapping("/{userId}/{addressId}")
    ResponseEntity<ResponseMessageUtilityDTO> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId
    );
}