package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface IDeliveryOrderService {
    ResponseMessageUtilityDTO assignDeliveryPartner(Long orderId, Long partnerUserId);
    ResponseMessageUtilityDTO confirmCodCollection(Long orderId, String authenticatedEmail);
}
