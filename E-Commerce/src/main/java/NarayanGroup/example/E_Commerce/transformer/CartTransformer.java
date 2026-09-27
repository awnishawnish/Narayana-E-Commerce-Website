package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.response.CartItemResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CartResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductSummaryDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import NarayanGroup.example.E_Commerce.model.Entity.CartItem;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CartTransformer {

    private final IS3Service s3Service;

    public CartResponseDTO toCartResponseDTO(Cart cart) {

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .totalItems(
                        cart.getCartItems()
                                .stream()
                                .filter(item -> !item.isDeleted())
                                .mapToInt(CartItem::getQuantity)
                                .sum()
                )
                .totalAmount(cart.getTotalAmount())
                .items(
                        cart.getCartItems()
                                .stream()
                                .filter(item -> !item.isDeleted())
                                .map(this::toCartItemResponseDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }


    private  CartItemResponseDTO toCartItemResponseDTO(
            CartItem item) {

        ProductSummaryDTO productDTO =
                ProductSummaryDTO.builder()
                        .productId(
                                item.getProduct().getId()
                        )
                        .productName(
                                item.getProduct().getTitle()
                        )
                        .category(
                                item.getProduct().getCategory()
                        )
                        .price(
                                item.getProduct().getPrice()
                        )
                        .imageKey(
                                s3Service.getImageUrl(
                                        item.getProduct().getImageKey()
                                )
                        )
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