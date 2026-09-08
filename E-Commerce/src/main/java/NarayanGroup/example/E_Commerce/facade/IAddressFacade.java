package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface IAddressFacade {

    ResponseMessageUtilityDTO addAddress(
            Long userId,
            AddressRequestDTO request
    );

    ResponseMessageUtilityDTO getAddresses(Long userId);

    ResponseMessageUtilityDTO updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDTO request
    );

    ResponseMessageUtilityDTO deleteAddress(
            Long userId,
            Long addressId
    );
}
