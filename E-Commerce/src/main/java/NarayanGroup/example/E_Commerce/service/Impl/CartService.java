package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import NarayanGroup.example.E_Commerce.model.Entity.CartItem;
import NarayanGroup.example.E_Commerce.model.Repositry.ICartItemRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.ICartRepository;
import NarayanGroup.example.E_Commerce.service.ICartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartService implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;

    @Override
    public Cart findByUserid(Long userId) {

        return cartRepository
                .findByUserIdAndIsDeletedFalse(userId)
                .orElse(null);
    }

    @Override
    public void addToCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItemById(Long cartItemId) {

        return cartItemRepository
                .findByIdAndIsDeletedFalse(cartItemId)
                .orElse(null);
    }

    @Override
    public void saveCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(CartItem cartItem) {

        /*
         * Soft delete
         */
        cartItem.setDeleted(true);

        cartItemRepository.save(cartItem);
    }
}