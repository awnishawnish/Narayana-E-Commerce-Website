package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.ISellerController;
import NarayanGroup.example.E_Commerce.facade.ISellerFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class SellerController implements ISellerController {
    private final ISellerFacade sellerFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> registerSeller(@Valid @RequestBody SellerRegistrationRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(201).body(sellerFacade.registerSeller(authentication.getName(), request));
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(sellerFacade.getProfile(authentication.getName()));
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> switchToVendor(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(sellerFacade.switchToVendor(authentication.getName(), response));
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> switchToUser(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(sellerFacade.switchToUser(authentication.getName(), response));
    }
}
