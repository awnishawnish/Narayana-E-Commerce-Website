package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IDeliveryOrderController;
import NarayanGroup.example.E_Commerce.facade.IDeliveryOrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryOrderController implements IDeliveryOrderController {
    private final IDeliveryOrderFacade deliveryOrderFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> assignDeliveryPartner(Long orderId, Long partnerUserId) {
        ResponseMessageUtilityDTO result = deliveryOrderFacade.assignDeliveryPartner(orderId, partnerUserId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> confirmCodCollection(Long orderId, Authentication authentication) {
        String email = authentication == null ? null : authentication.getName();
        ResponseMessageUtilityDTO result = deliveryOrderFacade.confirmCodCollection(orderId, email);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
