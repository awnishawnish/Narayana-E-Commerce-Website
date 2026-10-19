package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface ISellerFacade {
    ResponseMessageUtilityDTO registerSeller(String email, SellerRegistrationRequestDTO request);
    ResponseMessageUtilityDTO getProfile(String email);
    ResponseMessageUtilityDTO switchToVendor(String email, HttpServletResponse response);
    ResponseMessageUtilityDTO switchToUser(String email, HttpServletResponse response);
}
