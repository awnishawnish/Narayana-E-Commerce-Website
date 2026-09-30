package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.ICartFacade;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {
    private final ICartFacade facade = mock(ICartFacade.class);
    private final CartController controller = new CartController(facade);

    @Test void addToCart_shouldReturnCreated() {
        var request=AddCartRequestDTO.builder().build(); var body=ResponseMessageUtilityDTO.builder().build();
        when(facade.addToCart(1L,request)).thenReturn(body);
        var response=controller.addToCart(1L,request);
        assertEquals(HttpStatus.CREATED,response.getStatusCode()); assertSame(body,response.getBody());
        verify(facade).addToCart(1L,request);
    }
    @Test void getCart_shouldReturnOk() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.getCart(1L)).thenReturn(body);
        var response=controller.getCart(1L);
        assertEquals(HttpStatus.OK,response.getStatusCode()); assertSame(body,response.getBody());
    }
    @Test void updateQuantity_shouldReturnOk() {
        var request=UpdateCartItemRequestDTO.builder().quantity(2).build(); var body=ResponseMessageUtilityDTO.builder().build();
        when(facade.updateQuantity(1L,2L,request)).thenReturn(body);
        var response=controller.updateQuantity(1L,2L,request);
        assertEquals(HttpStatus.OK,response.getStatusCode()); assertSame(body,response.getBody());
        verify(facade).updateQuantity(1L,2L,request);
    }
    @Test void removeItem_shouldReturnOk() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.removeItem(1L,2L)).thenReturn(body);
        var response=controller.removeItem(1L,2L);
        assertEquals(HttpStatus.OK,response.getStatusCode()); assertSame(body,response.getBody());
    }
    @Test void clearCart_shouldReturnOk() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.clearCart(1L)).thenReturn(body);
        var response=controller.clearCart(1L);
        assertEquals(HttpStatus.OK,response.getStatusCode()); assertSame(body,response.getBody());
    }
}
