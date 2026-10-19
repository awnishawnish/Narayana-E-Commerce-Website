package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IDeliveryOrderFacade;
import NarayanGroup.example.E_Commerce.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryOrderFacade implements IDeliveryOrderFacade {
    private final IDeliveryOrderService deliveryOrderService;

    @Override
    public ResponseMessageUtilityDTO assignDeliveryPartner(Long orderId, Long partnerUserId) {
        return deliveryOrderService.assignDeliveryPartner(orderId, partnerUserId);
    }

    @Override
    public ResponseMessageUtilityDTO confirmCodCollection(Long orderId, String authenticatedEmail) {
        return deliveryOrderService.confirmCodCollection(orderId, authenticatedEmail);
    }
}
