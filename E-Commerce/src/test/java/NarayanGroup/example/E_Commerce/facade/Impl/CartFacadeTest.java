package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CartResponseDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.service.*;
import NarayanGroup.example.E_Commerce.transformer.CartTransformer;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartFacadeTest {
    private final ICartService cartService=mock(ICartService.class);
    private final IProductService productService=mock(IProductService.class);
    private final IUserService userService=mock(IUserService.class);
    private final CartTransformer transformer=mock(CartTransformer.class);
    private final CartFacade facade=new CartFacade(cartService,productService,userService,transformer);

    private final UserEntity user=UserEntity.builder().id(1L).email("u@test.com").build();
    private Product product(long id,int qty) {
        return Product.builder().id(id).title("P"+id).price(new BigDecimal("100")).quantity(qty).currency("INR").build();
    }
    private AddCartRequestDTO add(long productId,int qty) {
        return new AddCartRequestDTO(List.of(new AddCartItemRequestDTO(productId,qty)));
    }
    private CartItem item(Cart cart,Product product,int qty,boolean deleted) {
        return CartItem.builder().id(10L).cart(cart).product(product).quantity(qty)
                .priceAtAddition(product.getPrice()).currency(product.getCurrency()).isDeleted(deleted).build();
    }

    @Test void addToCart_shouldCreateNewCartAndCalculateTotals() {
        Product p=product(10,5); when(userService.getUserById(1L)).thenReturn(user); when(cartService.findByUserid(1L)).thenReturn(null);
        when(productService.getProductById(10L)).thenReturn(p); when(transformer.toCartResponseDTO(any())).thenReturn(CartResponseDTO.builder().build());
        var r=facade.addToCart(1L,add(10,2));
        assertEquals(201,r.getHttpStatus()); assertEquals(CommonConstants.PRODUCT_ADDED_SUCCESSFULLY,r.getMessage());
        verify(cartService).addToCart(argThat(c->c.getTotalItems()==2 && new BigDecimal("200").compareTo(c.getTotalAmount())==0));
    }

    @Test void addToCart_shouldIncreaseExistingItem() {
        Product p=product(10,5); Cart cart=Cart.builder().id(1L).user(user).cartItems(new ArrayList<>()).build(); CartItem existing=item(cart,p,2,false); cart.getCartItems().add(existing);
        when(userService.getUserById(1L)).thenReturn(user); when(cartService.findByUserid(1L)).thenReturn(cart); when(productService.getProductById(10L)).thenReturn(p); when(transformer.toCartResponseDTO(cart)).thenReturn(CartResponseDTO.builder().build());
        facade.addToCart(1L,add(10,2));
        assertEquals(4,existing.getQuantity()); assertEquals(4,cart.getTotalItems());
    }

    @Test void addToCart_shouldRejectNullOrEmptyProducts() {
        when(userService.getUserById(1L)).thenReturn(user); when(cartService.findByUserid(1L)).thenReturn(null);
        assertThrows(CustomException.InvalidQuantityException.class,()->facade.addToCart(1L,AddCartRequestDTO.builder().build()));
        verifyNoInteractions(productService);
    }

    @Test void addToCart_shouldRejectInvalidQuantityAndStock() {
        Product p=product(10,5); when(userService.getUserById(1L)).thenReturn(user); when(cartService.findByUserid(1L)).thenReturn(null); when(productService.getProductById(10L)).thenReturn(p);
        assertThrows(CustomException.InvalidQuantityException.class,()->facade.addToCart(1L,add(10,0)));
        assertThrows(CustomException.InsufficientStockException.class,()->facade.addToCart(1L,add(10,6)));
    }

    @Test void getCart_shouldRejectMissingCartAndCalculateExistingCart() {
        when(cartService.findByUserid(1L)).thenReturn(null);
        assertThrows(CustomException.CartNotFoundException.class,()->facade.getCart(1L));

        Product p=product(10,5); Cart cart=Cart.builder().id(1L).cartItems(new ArrayList<>()).build(); cart.getCartItems().add(item(cart,p,2,false));
        when(cartService.findByUserid(1L)).thenReturn(cart); when(transformer.toCartResponseDTO(cart)).thenReturn(CartResponseDTO.builder().build());
        var r=facade.getCart(1L);
        assertEquals(2,cart.getTotalItems()); assertEquals(200,cart.getTotalAmount().intValue()); assertEquals(200,r.getHttpStatus());
    }

    @Test void updateQuantity_shouldCoverValidationOwnershipAndStock() {
        assertThrows(CustomException.InvalidQuantityException.class,()->facade.updateQuantity(1L,10L,null));
        assertThrows(CustomException.InvalidQuantityException.class,()->facade.updateQuantity(1L,10L,UpdateCartItemRequestDTO.builder().quantity(0).build()));

        when(cartService.getCartItemById(10L)).thenReturn(null);
        assertThrows(CustomException.CartItemNotFoundException.class,()->facade.updateQuantity(1L,10L,UpdateCartItemRequestDTO.builder().quantity(2).build()));

        Product p=product(10,5); UserEntity other=UserEntity.builder().id(2L).build(); Cart cart=Cart.builder().user(other).cartItems(new ArrayList<>()).build();
        CartItem ci=item(cart,p,1,false); when(cartService.getCartItemById(10L)).thenReturn(ci);
        assertThrows(CustomException.UnauthorizedCartAccessException.class,()->facade.updateQuantity(1L,10L,UpdateCartItemRequestDTO.builder().quantity(2).build()));

        cart.setUser(user); when(cartService.getCartItemById(10L)).thenReturn(ci);
        assertThrows(CustomException.InsufficientStockException.class,()->facade.updateQuantity(1L,10L,UpdateCartItemRequestDTO.builder().quantity(6).build()));
    }

    @Test void updateQuantity_shouldSaveAndRecalculate() {
        Product p=product(10,5); Cart cart=Cart.builder().id(1L).user(user).cartItems(new ArrayList<>()).build(); CartItem ci=item(cart,p,1,false); cart.getCartItems().add(ci);
        when(cartService.getCartItemById(10L)).thenReturn(ci); when(transformer.toCartResponseDTO(cart)).thenReturn(CartResponseDTO.builder().build());
        var r=facade.updateQuantity(1L,10L,UpdateCartItemRequestDTO.builder().quantity(3).build());
        assertEquals(3,ci.getQuantity()); assertEquals(3,cart.getTotalItems()); assertEquals(200,r.getHttpStatus());
        verify(cartService).saveCartItem(ci); verify(cartService).addToCart(cart);
    }

    @Test void removeItem_shouldCoverNotFoundUnauthorizedAndSuccess() {
        when(cartService.getCartItemById(10L)).thenReturn(null);
        assertThrows(CustomException.CartItemNotFoundException.class,()->facade.removeItem(1L,10L));

        Product p=product(10,5); UserEntity other=UserEntity.builder().id(2L).build(); Cart cart=Cart.builder().user(other).cartItems(new ArrayList<>()).build(); CartItem ci=item(cart,p,1,false);
        when(cartService.getCartItemById(10L)).thenReturn(ci);
        assertThrows(CustomException.UnauthorizedCartAccessException.class,()->facade.removeItem(1L,10L));

        cart.setUser(user); cart.getCartItems().add(ci); when(transformer.toCartResponseDTO(cart)).thenReturn(CartResponseDTO.builder().build());
        var r=facade.removeItem(1L,10L);
        assertTrue(ci.isDeleted()); assertEquals(0,cart.getTotalItems()); assertEquals(200,r.getHttpStatus());
    }

    @Test void clearCart_shouldRejectMissingCartAndSoftDeleteItems() {
        when(cartService.findByUserid(1L)).thenReturn(null);
        assertThrows(CustomException.CartNotFoundException.class,()->facade.clearCart(1L));

        Product p=product(10,5); Cart cart=Cart.builder().user(user).cartItems(new ArrayList<>()).build(); CartItem a=item(cart,p,2,false); CartItem b=item(cart,p,1,false); cart.getCartItems().addAll(List.of(a,b));
        when(cartService.findByUserid(1L)).thenReturn(cart);
        var r=facade.clearCart(1L);
        assertTrue(a.isDeleted()); assertTrue(b.isDeleted()); assertEquals(0,cart.getTotalItems()); assertEquals(BigDecimal.ZERO,cart.getTotalAmount());
        assertEquals(CommonConstants.CART_CLEARED_SUCCESSFULLY,r.getMessage()); verify(cartService).addToCart(cart);
    }

    @Test void addToCart_shouldRejectMissingUserAndExistingItemStockOverflow() {
        when(userService.getUserById(1L)).thenReturn(null);
        assertThrows(CustomException.UserNotFoundException.class,()->facade.addToCart(1L,add(10,1)));

        Product p=product(10,3); Cart cart=Cart.builder().user(user).cartItems(new ArrayList<>()).build();
        cart.getCartItems().add(item(cart,p,2,false));
        when(userService.getUserById(1L)).thenReturn(user); when(cartService.findByUserid(1L)).thenReturn(cart); when(productService.getProductById(10L)).thenReturn(p);
        assertThrows(CustomException.InsufficientStockException.class,()->facade.addToCart(1L,add(10,2)));
    }

}
