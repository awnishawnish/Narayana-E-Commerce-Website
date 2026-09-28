package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IPaymentController;
import NarayanGroup.example.E_Commerce.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
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
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PAYMENT_VERIFIED_SUCCESSFULLY)
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
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PAYMENT_FAILURE_RECORDED)
                .build());
    }
}
