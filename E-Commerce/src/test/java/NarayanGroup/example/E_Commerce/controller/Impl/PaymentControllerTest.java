package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.service.IPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {
    @Mock IPaymentService paymentService;

    @Test
    void verifyPayment_shouldReturnOk() {
        PaymentController controller = new PaymentController(paymentService);
        PaymentVerificationRequestDTO request = PaymentVerificationRequestDTO.builder()
                .orderId(1L)
                .razorpayOrderId("o")
                .razorpayPaymentId("p")
                .razorpaySignature("s")
                .build();

        var response = controller.verifyPayment(7L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CommonConstants.SUCCESS, response.getBody().getStatus());
        assertEquals(CommonConstants.PAYMENT_VERIFIED_SUCCESSFULLY, response.getBody().getMessage());
        verify(paymentService).verifyRazorpayPayment(7L, request);
    }

    @Test
    void paymentFailed_shouldReturnOk() {
        PaymentController controller = new PaymentController(paymentService);

        var response = controller.paymentFailed(7L, 1L, "Payment declined");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CommonConstants.SUCCESS, response.getBody().getStatus());
        assertEquals(CommonConstants.PAYMENT_FAILURE_RECORDED, response.getBody().getMessage());
        verify(paymentService).markRazorpayPaymentFailed(7L, 1L, "Payment declined");
    }
}
