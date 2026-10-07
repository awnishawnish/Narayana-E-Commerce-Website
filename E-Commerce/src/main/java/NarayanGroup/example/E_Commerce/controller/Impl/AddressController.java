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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AddressController.class);

    private final IAddressFacade addressFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addAddress(
            Long userId,
            AddressRequestDTO request) {

        logger.info("addAddress called for userId={}", userId);

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

        logger.info("getAddresses called for userId={}", userId);

        return ResponseEntity.ok(
                addressFacade.getAddresses(userId)
        );
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> updateAddress(
            Long userId,
            Long addressId,
            AddressRequestDTO request) {

        logger.info("updateAddress called for userId={} addressId={}", userId, addressId);

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

        logger.info("deleteAddress called for userId={} addressId={}", userId, addressId);

        return ResponseEntity.ok(
                addressFacade.deleteAddress(
                        userId,
                        addressId
                )
        );
    }
}