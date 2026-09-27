package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Payment;

public interface IPaymentService {
    Payment verifyRazorpayPayment(Long userId, PaymentVerificationRequestDTO request);

    Payment markRazorpayPaymentFailed(Long userId, Long orderId, String reason);
}
