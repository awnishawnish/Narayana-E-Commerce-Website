package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery/orders")
public interface IDeliveryOrderController {
    @PostMapping("/{orderId}/assign/{partnerUserId}") ResponseEntity<ResponseMessageUtilityDTO> assignDeliveryPartner(@PathVariable Long orderId, @PathVariable Long partnerUserId);
    @PostMapping("/{orderId}/cod-collected") ResponseEntity<ResponseMessageUtilityDTO> confirmCodCollection(@PathVariable Long orderId, Authentication authentication);
}
