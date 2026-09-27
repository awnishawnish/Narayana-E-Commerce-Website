package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.PaymentVerificationRequestDTO;
import NarayanGroup.example.E_Commerce.service.IPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {
    @Mock IPaymentService paymentService;

    @Test void verifyPayment_shouldReturnOk() {
        PaymentController controller = new PaymentController(paymentService);
        PaymentVerificationRequestDTO request = PaymentVerificationRequestDTO.builder().orderId(1L).razorpayOrderId("o").razorpayPaymentId("p").razorpaySignature("s").build();
        var response = controller.verifyPayment(7L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody().getStatus());
        verify(paymentService).verifyRazorpayPayment(7L, request);
    }
}
