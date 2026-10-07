package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface ISellerFacade {
    ResponseMessageUtilityDTO registerSeller(String email, SellerRegistrationRequestDTO request);
    ResponseMessageUtilityDTO getProfile(String email);
}
