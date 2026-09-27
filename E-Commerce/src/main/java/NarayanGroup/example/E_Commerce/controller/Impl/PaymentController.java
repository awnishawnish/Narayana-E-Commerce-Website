package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IPaymentController;
import NarayanGroup.example.E_Commerce.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController implements IPaymentController {
    private final IPaymentService paymentService;

    @PostMapping("/{userId}/verify")
    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> verifyPayment(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentVerificationRequestDTO request) {
        paymentService.verifyRazorpayPayment(userId, request);
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Payment verified successfully")
                .build());
    }

    @PostMapping("/{userId}/failed/{orderId}")
    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> paymentFailed(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        paymentService.markRazorpayPaymentFailed(userId, orderId, reason);
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Payment failure recorded")
                .build());
    }
}
