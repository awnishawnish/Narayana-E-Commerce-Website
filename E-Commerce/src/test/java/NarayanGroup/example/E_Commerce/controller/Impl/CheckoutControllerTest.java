package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.facade.ICheckoutFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @Mock
    ICheckoutFacade facade;

    @Test
    void checkout_shouldDelegateAndReturnCreated() {
        CheckoutRequestDTO request = CheckoutRequestDTO.builder()
                .paymentMethod(CommonConstants.COD)
                .build();
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .build();
        when(facade.checkout(1L, request)).thenReturn(body);

        var response = new CheckoutController(facade).checkout(1L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(body, response.getBody());
        verify(facade).checkout(1L, request);
    }
}
