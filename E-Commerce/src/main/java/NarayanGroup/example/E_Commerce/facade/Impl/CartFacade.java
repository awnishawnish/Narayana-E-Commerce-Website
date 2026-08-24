package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CartResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.facade.ICartFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import NarayanGroup.example.E_Commerce.model.Entity.CartItem;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.ICartService;
import NarayanGroup.example.E_Commerce.service.IProductService;
import NarayanGroup.example.E_Commerce.service.IUserService;
import NarayanGroup.example.E_Commerce.transformer.CartTransformer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@AllArgsConstructor
class CartFacade implements ICartFacade {

    private final ICartService cartService;
    private final IProductService productService;
    private final IUserService userService;
    private final CartTransformer cartTransformer;


    // ============================================================
    // ADD TO CART
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO addToCart(
            Long userId,
            AddCartRequestDTO request) {

        UserEntity user = userService.getUserById(userId);

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {

            cart = new Cart();

            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart.setTotalItems(0);
            cart.setTotalAmount(BigDecimal.ZERO);
        }

        for (AddCartItemRequestDTO productRequest : request.getProducts()) {

            Product product = productService.getProductById(
                    productRequest.getProductId()
            );

            if (product == null) {
                throw new CustomException.ProductNotFoundException(
                        "Product not found with id: "
                                + productRequest.getProductId()
                );
            }

            int requestedQuantity = productRequest.getQuantity();

            if (requestedQuantity <= 0) {
                throw new CustomException.InvalidQuantityException(
                        "Quantity must be greater than zero"
                );
            }

            if (requestedQuantity > product.getQuantity()) {
                throw new CustomException.InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getTitle()
                );
            }

            CartItem existingItem = cart.getCartItems()
                    .stream()
                    .filter(item ->
                            item.getProduct().getId()
                                    .equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {

                int newQuantity =
                        existingItem.getQuantity() + requestedQuantity;

                if (newQuantity > product.getQuantity()) {
                    throw new CustomException.InsufficientStockException(
                            "Insufficient stock for product: "
                                    + product.getTitle()
                    );
                }

                existingItem.setQuantity(newQuantity);

            } else {

                CartItem cartItem = new CartItem();

                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(requestedQuantity);
                cartItem.setPrice_at_addition(product.getPrice());
                cartItem.setCurrency(product.getCurrency());

                cart.getCartItems().add(cartItem);
            }
        }

        calculateCartTotals(cart);


        CartResponseDTO responseDTO =  CartTransformer.toCartResponseDTO(cart);

        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(201)
                .message("Product added successfully")
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // GET CART
    // ============================================================

    @Override
    public ResponseMessageUtilityDTO getCart(Long userId) {

//        Long userId = getLoggedInUserId();

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {
            throw new CustomException.CartNotFoundException(
                    "Cart not found for user: " + userId
            );
        }

        calculateCartTotals(cart);
        CartResponseDTO responseDTO =  CartTransformer.toCartResponseDTO(cart);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Cart fetched successfully")
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // UPDATE CART ITEM QUANTITY
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO updateQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDTO request) {

        if (request.getQuantity() <= 0) {
            throw new CustomException.InvalidQuantityException(
                    "Quantity must be greater than zero"
            );
        }

        CartItem cartItem =
                cartService.getCartItemById(cartItemId);

        if (cartItem == null) {
            throw new CustomException.CartItemNotFoundException(
                    "Cart item not found with id: " + cartItemId
            );
        }



        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(userId)) {

            throw new CustomException.UnauthorizedCartAccessException(
                    "You are not authorized to modify this cart item"
            );
        }

        Product product = cartItem.getProduct();

        if (request.getQuantity() > product.getQuantity()) {
            throw new CustomException.InsufficientStockException(
                    "Insufficient stock for product: "
                            + product.getTitle()
            );
        }

        cartItem.setQuantity(request.getQuantity());

        Cart cart = cartItem.getCart();

        calculateCartTotals(cart);

        cartService.saveCartItem(cartItem);
        cartService.addToCart(cart);
        CartResponseDTO responseDTO =  CartTransformer.toCartResponseDTO(cart);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Cart item quantity updated successfully")
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // REMOVE CART ITEM
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO removeItem(Long userId,Long cartItemId) {

        CartItem cartItem =
                cartService.getCartItemById(cartItemId);

        if (cartItem == null) {
            throw new CustomException.CartItemNotFoundException(
                    "Cart item not found with id: " + cartItemId
            );
        }



        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(userId)) {

            throw new CustomException.UnauthorizedCartAccessException(
                    "You are not authorized to modify this cart"
            );
        }

        Cart cart = cartItem.getCart();

        cart.getCartItems().remove(cartItem);

        cartService.deleteCartItem(cartItem);

        calculateCartTotals(cart);

        cartService.addToCart(cart);
        CartResponseDTO responseDTO =  CartTransformer.toCartResponseDTO(cart);
        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Product removed from cart successfully")
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // CLEAR CART
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO clearCart(Long userId) {

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {
            throw new CustomException.CartNotFoundException(
                    "Cart not found"
            );
        }

        cart.getCartItems().clear();

        cart.setTotalItems(0);
        cart.setTotalAmount(BigDecimal.ZERO);

        cartService.addToCart(cart);

        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(200)
                .message("Cart cleared successfully")
                .data(null)
                .build();
    }


    // ============================================================
    // CALCULATE CART TOTALS
    // ============================================================

    private void calculateCartTotals(Cart cart) {

        int totalItems = cart.getCartItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmount = cart.getCartItems()
                .stream()
                .map(item ->
                        item.getPrice_at_addition()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        cart.setTotalItems(totalItems);
        cart.setTotalAmount(totalAmount);
    }


    // ============================================================
    // GET LOGGED-IN USER ID
    // ============================================================

    private Long getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new CustomException.UnauthorizedException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        UserEntity user =
                userService.getUserByEmail(email);

        if (user == null) {
            throw new CustomException.UserNotFoundException(
                    "User not found with email: " + email
            );
        }

        return user.getId();
    }
}