package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import NarayanGroup.example.E_Commerce.model.Entity.CartItem;

public interface ICartService {

    Cart findByUserid(Long userId);

    void addToCart(Cart cart);

    CartItem getCartItemById(Long cartItemId);

    void saveCartItem(CartItem cartItem);

    void deleteCartItem(CartItem cartItem);
}