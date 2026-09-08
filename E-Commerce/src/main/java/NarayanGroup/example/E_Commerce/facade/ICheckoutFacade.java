package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface ICheckoutFacade {

    ResponseMessageUtilityDTO checkout(
            Long userId,
            CheckoutRequestDTO request
    );
}