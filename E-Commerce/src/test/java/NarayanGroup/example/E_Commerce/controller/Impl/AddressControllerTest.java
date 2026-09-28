package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IAddressFacade;
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
class AddressControllerTest {

    @Mock
    IAddressFacade facade;

    @Test
    void addAddress_shouldReturnCreated() {
        AddressRequestDTO request = AddressRequestDTO.builder().build();
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .build();
        when(facade.addAddress(1L, request)).thenReturn(body);

        var response = new AddressController(facade).addAddress(1L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(facade).addAddress(1L, request);
    }

    @Test
    void getAddresses_shouldReturnOk() {
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().build();
        when(facade.getAddresses(1L)).thenReturn(body);

        var response = new AddressController(facade).getAddresses(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(body, response.getBody());
    }

    @Test
    void updateAddress_shouldDelegate() {
        AddressRequestDTO request = AddressRequestDTO.builder().build();
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().build();
        when(facade.updateAddress(1L, 2L, request)).thenReturn(body);

        var response = new AddressController(facade).updateAddress(1L, 2L, request);

        assertSame(body, response.getBody());
        verify(facade).updateAddress(1L, 2L, request);
    }

    @Test
    void deleteAddress_shouldDelegate() {
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().build();
        when(facade.deleteAddress(1L, 2L)).thenReturn(body);

        var response = new AddressController(facade).deleteAddress(1L, 2L);

        assertSame(body, response.getBody());
        verify(facade).deleteAddress(1L, 2L);
    }
}
