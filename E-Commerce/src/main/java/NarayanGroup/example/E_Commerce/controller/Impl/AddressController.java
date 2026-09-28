package NarayanGroup.example.E_Commerce.controller.Impl;



import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IAddressController;
import NarayanGroup.example.E_Commerce.facade.IAddressFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@RestController
@AllArgsConstructor
public class AddressController implements IAddressController {

    private final IAddressFacade addressFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addAddress(
            Long userId,
            AddressRequestDTO request) {

        return ResponseEntity
                .status(201)
                .body(
                        addressFacade.addAddress(
                                userId,
                                request
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getAddresses(
            Long userId) {

        return ResponseEntity.ok(
                addressFacade.getAddresses(userId)
        );
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDTO request) {

        return ResponseEntity.ok(
                addressFacade.updateAddress(
                        userId,
                        addressId,
                        request
                )
        );
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> deleteAddress(
            Long userId,
            Long addressId) {

        return ResponseEntity.ok(
                addressFacade.deleteAddress(
                        userId,
                        addressId
                )
        );
    }
}