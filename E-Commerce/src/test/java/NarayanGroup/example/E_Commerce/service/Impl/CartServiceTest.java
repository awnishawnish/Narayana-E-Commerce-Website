package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Repositry.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    @Test void cartOperations_shouldWork() {
        ICartRepository carts=mock(ICartRepository.class); ICartItemRepository items=mock(ICartItemRepository.class);
        Cart cart=Cart.builder().id(1L).build(); CartItem item=CartItem.builder().id(2L).cart(cart).build();
        when(carts.findByUserIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(cart)); when(items.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(item));
        CartService service=new CartService(carts,items);
        assertSame(cart,service.findByUserid(1L)); assertSame(item,service.getCartItemById(2L)); service.addToCart(cart); service.saveCartItem(item); service.deleteCartItem(item); service.removeItem(1L,2L);
        assertTrue(item.isDeleted()); verify(carts).save(cart); verify(items,atLeast(2)).save(item);
    }
    @Test void removeItem_shouldIgnoreWrongCart() { ICartRepository carts=mock(ICartRepository.class); ICartItemRepository items=mock(ICartItemRepository.class); Cart c=Cart.builder().id(1L).build(); Cart other=Cart.builder().id(2L).build(); CartItem i=CartItem.builder().id(3L).cart(other).build(); when(carts.findByUserIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(c)); when(items.findByIdAndIsDeletedFalse(3L)).thenReturn(Optional.of(i)); new CartService(carts,items).removeItem(1L,3L); verify(items,never()).save(any()); }
}
