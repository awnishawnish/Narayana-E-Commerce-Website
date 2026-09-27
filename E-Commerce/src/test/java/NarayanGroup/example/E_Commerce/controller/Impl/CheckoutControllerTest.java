package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.ICheckoutFacade;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutControllerTest {
    @Test void checkout_shouldDelegateAndReturnCreated() {
        ICheckoutFacade facade = mock(ICheckoutFacade.class);
        CheckoutRequestDTO request = CheckoutRequestDTO.builder().paymentMethod("COD").build();
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().status("Success").build();
        when(facade.checkout(1L, request)).thenReturn(body);

        var response = new CheckoutController(facade).checkout(1L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(body, response.getBody());
        verify(facade).checkout(1L, request);
    }
}
