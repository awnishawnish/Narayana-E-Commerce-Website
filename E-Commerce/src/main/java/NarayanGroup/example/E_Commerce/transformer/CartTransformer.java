package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.response.CartItemResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CartResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductSummaryDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import NarayanGroup.example.E_Commerce.model.Entity.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CartTransformer {

    public static CartResponseDTO toCartResponseDTO(Cart cart) {

      List<CartItemResponseDTO> CartItemResponseDTO= cart.getCartItems()
                .stream()
                .filter(item -> !item.isDeleted())
                .map(CartTransformer::toCartItemResponseDTO)
                .toList();

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .items(CartItemResponseDTO)
                .build();
    }

    private static CartItemResponseDTO toCartItemResponseDTO(
            CartItem item) {

        ProductSummaryDTO productDTO =
                ProductSummaryDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getTitle())
                        .category(item.getProduct().getCategory())
                        .price(item.getProduct().getPrice())
                        .imageKey(item.getProduct().getImageKey())
                        .build();

        return CartItemResponseDTO.builder()
                .cartItemId(item.getId())
                .product(productDTO)
                .quantity(item.getQuantity())
                .priceAtAddition(item.getPriceAtAddition())
                .currency(item.getCurrency())
                .build();
    }
}