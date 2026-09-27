package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.http.ResponseEntity;

public interface IOrderController {
    ResponseEntity<ResponseMessageUtilityDTO> getOrders(Long userId);
    ResponseEntity<ResponseMessageUtilityDTO> getOrder(Long userId, Long orderId);
    ResponseEntity<ResponseMessageUtilityDTO> cancelOrder(Long userId, Long orderId, String reason);
    ResponseEntity<byte[]> invoice(Long userId, Long orderId);
}
