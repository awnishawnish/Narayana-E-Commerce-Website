package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartTransformerTest {
    @Test void toCartResponseDTO_shouldIgnoreDeletedItemsAndBuildImageUrl() {
        IS3Service s3 = mock(IS3Service.class);
        when(s3.getImageUrl("key")).thenReturn("url");
        Product product = Product.builder().id(1L).title("Phone").category("C").price(new BigDecimal("100")).imageKey("key").build();
        CartItem active = CartItem.builder().id(11L).product(product).quantity(2).currency("INR").priceAtAddition(new BigDecimal("100")).isDeleted(false).build();
        CartItem deleted = CartItem.builder().id(12L).product(product).quantity(3).isDeleted(true).build();
        Cart cart = Cart.builder().id(1L).totalAmount(new BigDecimal("200")).cartItems(Arrays.asList(active, deleted)).build();
        var result = new CartTransformer(s3).toCartResponseDTO(cart);
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getItems().size());
        assertEquals("url", result.getItems().get(0).getProduct().getImageKey());
    }
}
