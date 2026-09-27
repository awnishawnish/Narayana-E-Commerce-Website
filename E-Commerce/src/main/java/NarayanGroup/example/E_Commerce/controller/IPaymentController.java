package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.http.ResponseEntity;

public interface IPaymentController {
    ResponseEntity<ResponseMessageUtilityDTO> verifyPayment(Long userId, PaymentVerificationRequestDTO request);

    ResponseEntity<ResponseMessageUtilityDTO> paymentFailed(Long userId, Long orderId, String reason);
}
