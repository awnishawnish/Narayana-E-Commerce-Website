package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.AddressResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.facade.IAddressFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Address;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.IAddressService;
import NarayanGroup.example.E_Commerce.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressFacade implements IAddressFacade {

    private final IAddressService addressService;
    private final IUserService userService;

    @Override
    @Transactional
    public ResponseMessageUtilityDTO addAddress(
            Long userId,
            AddressRequestDTO request) {

        UserEntity user =
                userService.getUserById(userId);

        /*
         * If this address should be default,
         * remove default flag from existing addresses.
         */
        if (Boolean.TRUE.equals(request.getIsDefault())) {

            List<Address> addresses =
                    addressService
                            .findActiveAddressesByUserId(userId);

            addresses.forEach(address ->
                    address.setDefault(false));

            addresses.forEach(address ->
                    addressService.save(address));
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .isDefault(
                        Boolean.TRUE.equals(
                                request.getIsDefault()
                        )
                )
                .isDeleted(false)
                .build();

        Address saved =
                addressService.save(address);

        return success(
                "Address added successfully",
                toResponse(saved),
                201
        );
    }

    @Override
    public ResponseMessageUtilityDTO getAddresses(
            Long userId) {

        List<AddressResponseDTO> addresses =
                addressService
                        .findActiveAddressesByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return success(
                "Addresses fetched successfully",
                addresses,
                200
        );
    }

    @Override
    @Transactional
    public ResponseMessageUtilityDTO updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDTO request) {

        Address address =
                addressService.findActiveAddress(
                        addressId,
                        userId
                );

        if (Boolean.TRUE.equals(request.getIsDefault())) {

            List<Address> addresses =
                    addressService
                            .findActiveAddressesByUserId(userId);

            addresses.stream()
                    .filter(item ->
                            !item.getId().equals(addressId))
                    .forEach(item -> {
                        item.setDefault(false);
                        addressService.save(item);
                    });
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());

        if (request.getIsDefault() != null) {
            address.setDefault(
                    request.getIsDefault()
            );
        }

        Address updated =
                addressService.save(address);

        return success(
                "Address updated successfully",
                toResponse(updated),
                200
        );
    }

    @Override
    @Transactional
    public ResponseMessageUtilityDTO deleteAddress(
            Long userId,
            Long addressId) {

        Address address =
                addressService.findActiveAddress(
                        addressId,
                        userId
                );

        addressService.delete(address);

        return success(
                "Address deleted successfully",
                null,
                200
        );
    }

    private AddressResponseDTO toResponse(
            Address address) {

        return AddressResponseDTO.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .isDefault(address.isDefault())
                .build();
    }

    private ResponseMessageUtilityDTO success(
            String message,
            Object data,
            int status) {

        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(status)
                .message(message)
                .data(data)
                .build();
    }
}