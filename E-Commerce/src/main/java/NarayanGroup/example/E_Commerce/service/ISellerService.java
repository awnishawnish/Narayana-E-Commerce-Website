package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;

public interface ISellerService {

    Seller registerSeller(String email, SellerRegistrationRequestDTO request);

    Seller getSellerByUserId(Long userId);

    Seller getSellerByEmail(String email);

    Seller getActiveSellerByUserId(Long userId);

    boolean isActiveSeller(Long userId);
}
