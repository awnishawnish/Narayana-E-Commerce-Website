package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IAddressFacade;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressControllerTest {
    @Test void addAddress_shouldReturnCreated() {
        IAddressFacade facade = mock(IAddressFacade.class);
        AddressRequestDTO request = AddressRequestDTO.builder().build();
        ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().status("Success").build();
        when(facade.addAddress(1L, request)).thenReturn(body);
        var response = new AddressController(facade).addAddress(1L, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); verify(facade).addAddress(1L, request);
    }
    @Test void getAddresses_shouldReturnOk() {
        IAddressFacade facade = mock(IAddressFacade.class); ResponseMessageUtilityDTO body = ResponseMessageUtilityDTO.builder().build();
        when(facade.getAddresses(1L)).thenReturn(body);
        var response = new AddressController(facade).getAddresses(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode()); assertSame(body,response.getBody());
    }
    @Test void updateAddress_shouldDelegate() {
        IAddressFacade facade = mock(IAddressFacade.class); AddressRequestDTO request=AddressRequestDTO.builder().build(); ResponseMessageUtilityDTO body=ResponseMessageUtilityDTO.builder().build();
        when(facade.updateAddress(1L,2L,request)).thenReturn(body);
        assertSame(body,new AddressController(facade).updateAddress(1L,2L,request).getBody()); verify(facade).updateAddress(1L,2L,request);
    }
    @Test void deleteAddress_shouldDelegate() {
        IAddressFacade facade=mock(IAddressFacade.class); ResponseMessageUtilityDTO body=ResponseMessageUtilityDTO.builder().build(); when(facade.deleteAddress(1L,2L)).thenReturn(body);
        assertSame(body,new AddressController(facade).deleteAddress(1L,2L).getBody()); verify(facade).deleteAddress(1L,2L);
    }
}
