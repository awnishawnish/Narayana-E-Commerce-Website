package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CartResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
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

        log.debug("Starting addToCart for userId={}", userId);

        UserEntity user = userService.getUserById(userId);

        if (user == null) {
            log.warn("User not found while adding product to cart. userId={}", userId);

            throw new CustomException.UserNotFoundException(
                    ErrorConstants.USER_NOT_FOUND_WITH_ID + userId
            );
        }

        log.debug("User found successfully. userId={}", userId);

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {

            log.debug("No existing cart found for userId={}. Creating new cart.", userId);

            cart = new Cart();

            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart.setTotalItems(0);
            cart.setTotalAmount(BigDecimal.ZERO);

            log.debug("New cart initialized for userId={}", userId);
        } else {
            log.debug(
                    "Existing cart found for userId={} with itemCount={}",
                    userId,
                    cart.getCartItems() != null ? cart.getCartItems().size() : 0
            );
        }

        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {

            log.warn("Add to cart request contains no products. userId={}", userId);

            throw new CustomException.InvalidQuantityException(
                    ErrorConstants.CART_REQUEST_MUST_CONTAIN_PRODUCT
            );
        }

        log.debug(
                "Processing {} product(s) for userId={}",
                request.getProducts().size(),
                userId
        );

        for (AddCartItemRequestDTO productRequest : request.getProducts()) {

            Long productId = productRequest.getProductId();
            int requestedQuantity = productRequest.getQuantity();

            log.debug(
                    "Processing cart item. userId={}, productId={}, requestedQuantity={}",
                    userId,
                    productId,
                    requestedQuantity
            );

            Product product = productService.getProductById(productId);

            if (product == null) {

                log.warn(
                        "Product not found. userId={}, productId={}",
                        userId,
                        productId
                );

                throw new CustomException.ProductNotFoundException(
                        ErrorConstants.PRODUCT_NOT_FOUND_WITH_ID + productId
                );
            }

            log.debug(
                    "Product found. productId={}, title={}, availableQuantity={}, price={}",
                    product.getId(),
                    product.getTitle(),
                    product.getQuantity(),
                    product.getPrice()
            );

            if (requestedQuantity <= 0) {

                log.warn(
                        "Invalid quantity requested. userId={}, productId={}, quantity={}",
                        userId,
                        productId,
                        requestedQuantity
                );

                throw new CustomException.InvalidQuantityException(
                        ErrorConstants.INVALID_QUANTITY
                );
            }

            if (requestedQuantity > product.getQuantity()) {

                log.warn(
                        "Insufficient stock. userId={}, productId={}, requestedQuantity={}, availableQuantity={}",
                        userId,
                        productId,
                        requestedQuantity,
                        product.getQuantity()
                );

                throw new CustomException.InsufficientStockException(
                        ErrorConstants.INSUFFICIENT_STOCK_FOR_PRODUCT + product.getTitle()
                );
            }

            CartItem existingItem = cart.getCartItems()
                    .stream()
                    .filter(item ->
                            !item.isDeleted()
                                    && item.getProduct().getId()
                                    .equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {

                int currentQuantity = existingItem.getQuantity();
                int newQuantity = currentQuantity + requestedQuantity;

                log.debug(
                        "Existing cart item found. userId={}, productId={}, currentQuantity={}, requestedQuantity={}, newQuantity={}",
                        userId,
                        productId,
                        currentQuantity,
                        requestedQuantity,
                        newQuantity
                );

                if (newQuantity > product.getQuantity()) {

                    log.warn(
                            "Insufficient stock after quantity update. userId={}, productId={}, requestedQuantity={}, availableQuantity={}",
                            userId,
                            productId,
                            newQuantity,
                            product.getQuantity()
                    );

                    throw new CustomException.InsufficientStockException(
                            ErrorConstants.INSUFFICIENT_STOCK_FOR_PRODUCT + product.getTitle()
                    );
                }

                existingItem.setQuantity(newQuantity);

                log.debug(
                        "Existing cart item quantity updated. productId={}, newQuantity={}",
                        productId,
                        newQuantity
                );

            } else {

                log.debug(
                        "Product not present in cart. Creating new cart item. productId={}, quantity={}",
                        productId,
                        requestedQuantity
                );

                CartItem cartItem = new CartItem();

                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(requestedQuantity);
                cartItem.setPriceAtAddition(product.getPrice());
                cartItem.setCurrency(product.getCurrency());

                cart.getCartItems().add(cartItem);

                log.debug(
                        "New cart item added successfully. productId={}, quantity={}",
                        productId,
                        requestedQuantity
                );
            }
        }

        calculateCartTotals(cart);

        log.debug(
                "Cart totals calculated. userId={}, totalItems={}, totalAmount={}",
                userId,
                cart.getTotalItems(),
                cart.getTotalAmount()
        );

        cartService.addToCart(cart);

        log.debug("Cart saved successfully. userId={}", userId);

        CartResponseDTO responseDTO =
                cartTransformer.toCartResponseDTO(cart);

        log.debug("Cart response generated successfully. userId={}", userId);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.PRODUCT_ADDED_SUCCESSFULLY)
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // GET CART
    // ============================================================

    @Override
    public ResponseMessageUtilityDTO getCart(Long userId) {

        log.debug("Starting getCart for userId={}", userId);

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {

            log.warn("Cart not found for userId={}", userId);

            throw new CustomException.CartNotFoundException(
                    ErrorConstants.CART_NOT_FOUND_FOR_USER + userId
            );
        }

        log.debug("Cart found for userId={}", userId);

        calculateCartTotals(cart);

        log.debug(
                "Cart totals calculated. userId={}, totalItems={}, totalAmount={}",
                userId,
                cart.getTotalItems(),
                cart.getTotalAmount()
        );

        CartResponseDTO responseDTO =
                cartTransformer.toCartResponseDTO(cart);

        log.debug("Cart response generated successfully. userId={}", userId);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.CART_FETCHED_SUCCESSFULLY)
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

        log.debug(
                "Starting updateQuantity. userId={}, cartItemId={}",
                userId,
                cartItemId
        );

        if (request == null || request.getQuantity() <= 0) {

            log.warn(
                    "Invalid quantity for cart item update. userId={}, cartItemId={}, quantity={}",
                    userId,
                    cartItemId,
                    request != null ? request.getQuantity() : null
            );

            throw new CustomException.InvalidQuantityException(
                    ErrorConstants.INVALID_QUANTITY
            );
        }

        log.debug(
                "Requested quantity update. userId={}, cartItemId={}, newQuantity={}",
                userId,
                cartItemId,
                request.getQuantity()
        );

        CartItem cartItem =
                cartService.getCartItemById(cartItemId);

        if (cartItem == null) {

            log.warn(
                    "Cart item not found. userId={}, cartItemId={}",
                    userId,
                    cartItemId
            );

            throw new CustomException.CartItemNotFoundException(
                    ErrorConstants.CART_ITEM_NOT_FOUND + cartItemId
            );
        }

        log.debug(
                "Cart item found. cartItemId={}, productId={}, currentQuantity={}",
                cartItemId,
                cartItem.getProduct().getId(),
                cartItem.getQuantity()
        );

        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(userId)) {

            log.warn(
                    "Unauthorized cart item modification attempt. userId={}, cartItemId={}, cartOwnerId={}",
                    userId,
                    cartItemId,
                    cartItem.getCart().getUser().getId()
            );

            throw new CustomException.UnauthorizedCartAccessException(
                    ErrorConstants.UNAUTHORIZED_CART_ITEM
            );
        }

        Product product = cartItem.getProduct();

        log.debug(
                "Checking stock for quantity update. productId={}, requestedQuantity={}, availableQuantity={}",
                product.getId(),
                request.getQuantity(),
                product.getQuantity()
        );

        if (request.getQuantity() > product.getQuantity()) {

            log.warn(
                    "Insufficient stock for quantity update. userId={}, cartItemId={}, productId={}, requestedQuantity={}, availableQuantity={}",
                    userId,
                    cartItemId,
                    product.getId(),
                    request.getQuantity(),
                    product.getQuantity()
            );

            throw new CustomException.InsufficientStockException(
                    ErrorConstants.INSUFFICIENT_STOCK_FOR_PRODUCT + product.getTitle()
            );
        }

        cartItem.setQuantity(request.getQuantity());

        log.debug(
                "Cart item quantity updated in memory. cartItemId={}, newQuantity={}",
                cartItemId,
                request.getQuantity()
        );

        Cart cart = cartItem.getCart();

        calculateCartTotals(cart);

        log.debug(
                "Cart totals recalculated after quantity update. userId={}, totalItems={}, totalAmount={}",
                userId,
                cart.getTotalItems(),
                cart.getTotalAmount()
        );

        cartService.saveCartItem(cartItem);
        cartService.addToCart(cart);

        log.debug(
                "Cart item and cart saved successfully. userId={}, cartItemId={}",
                userId,
                cartItemId
        );

        CartResponseDTO responseDTO =
                cartTransformer.toCartResponseDTO(cart);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.CART_ITEM_QUANTITY_UPDATED_SUCCESSFULLY)
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // REMOVE CART ITEM
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO removeItem(
            Long userId,
            Long cartItemId) {

        log.debug(
                "Starting removeItem. userId={}, cartItemId={}",
                userId,
                cartItemId
        );

        CartItem cartItem =
                cartService.getCartItemById(cartItemId);

        if (cartItem == null) {

            log.warn(
                    "Cart item not found during removal. userId={}, cartItemId={}",
                    userId,
                    cartItemId
            );

            throw new CustomException.CartItemNotFoundException(
                    ErrorConstants.CART_ITEM_NOT_FOUND + cartItemId
            );
        }

        log.debug(
                "Cart item found. cartItemId={}, productId={}",
                cartItemId,
                cartItem.getProduct().getId()
        );

        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(userId)) {

            log.warn(
                    "Unauthorized cart item removal attempt. userId={}, cartItemId={}, cartOwnerId={}",
                    userId,
                    cartItemId,
                    cartItem.getCart().getUser().getId()
            );

            throw new CustomException.UnauthorizedCartAccessException(
                    ErrorConstants.UNAUTHORIZED_CART_ITEM
            );
        }

        Cart cart = cartItem.getCart();

        /*
         * Soft delete.
         *
         * DO NOT remove it from cart.getCartItems()
         * because orphanRemoval=true can physically delete it.
         */

        cartItem.setDeleted(true);

        log.debug(
                "Cart item marked as deleted. userId={}, cartItemId={}, productId={}",
                userId,
                cartItemId,
                cartItem.getProduct().getId()
        );

        calculateCartTotals(cart);

        log.debug(
                "Cart totals recalculated after item removal. userId={}, totalItems={}, totalAmount={}",
                userId,
                cart.getTotalItems(),
                cart.getTotalAmount()
        );

        cartService.saveCartItem(cartItem);
        cartService.addToCart(cart);

        log.debug(
                "Cart item removal saved successfully. userId={}, cartItemId={}",
                userId,
                cartItemId
        );

        CartResponseDTO responseDTO =
                cartTransformer.toCartResponseDTO(cart);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PRODUCT_REMOVED_FROM_CART_SUCCESSFULLY)
                .data(responseDTO)
                .build();
    }


    // ============================================================
    // CLEAR CART
    // ============================================================

    @Transactional
    @Override
    public ResponseMessageUtilityDTO clearCart(Long userId) {

        log.debug("Starting clearCart for userId={}", userId);

        Cart cart = cartService.findByUserid(userId);

        if (cart == null) {

            log.warn("Cart not found while clearing cart. userId={}", userId);

            throw new CustomException.CartNotFoundException(
                    ErrorConstants.CART_NOT_FOUND
            );
        }

        log.debug(
                "Cart found. userId={}, itemCount={}",
                userId,
                cart.getCartItems() != null
                        ? cart.getCartItems().size()
                        : 0
        );

        cart.getCartItems()
                .forEach(item -> item.setDeleted(true));

        cart.setTotalItems(0);
        cart.setTotalAmount(BigDecimal.ZERO);

        log.debug(
                "All cart items marked as deleted and totals reset. userId={}",
                userId
        );

        cartService.addToCart(cart);

        log.debug("Cart cleared successfully. userId={}", userId);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.CART_CLEARED_SUCCESSFULLY)
                .data(null)
                .build();
    }


    // ============================================================
    // CALCULATE CART TOTALS
    // ============================================================

    private void calculateCartTotals(Cart cart) {

        if (cart == null || cart.getCartItems() == null) {

            log.warn("Cannot calculate cart totals because cart or cart items are null");

            return;
        }

        log.debug(
                "Calculating cart totals. cartId={}, itemCount={}",
                cart.getId(),
                cart.getCartItems().size()
        );

        int totalItems = cart.getCartItems()
                .stream()
                .filter(item -> !item.isDeleted())
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmount = cart.getCartItems()
                .stream()
                .filter(item -> !item.isDeleted())
                .map(item ->
                        item.getPriceAtAddition()
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

        log.debug(
                "Cart totals calculated successfully. cartId={}, totalItems={}, totalAmount={}",
                cart.getId(),
                totalItems,
                totalAmount
        );
    }


    // ============================================================
    // GET LOGGED-IN USER ID
    // ============================================================

    private Long getLoggedInUserId() {

        log.debug("Attempting to retrieve logged-in user");

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            log.warn(ErrorConstants.USER_AUTHENTICATION_FAILED);

            throw new CustomException.UnauthorizedException(
                    ErrorConstants.USER_NOT_AUTHENTICATED
            );
        }

        String email = authentication.getName();

        log.debug(
                "Authenticated user identified by email={}",
                email
        );

        UserEntity user =
                userService.getUserByEmail(email);

        if (user == null) {

            log.warn(
                    "Authenticated user not found in database. email={}",
                    email
            );

            throw new CustomException.UserNotFoundException(
                    ErrorConstants.USER_NOT_FOUND_WITH_EMAIL + email
            );
        }

        log.debug(
                "Logged-in user resolved successfully. userId={}",
                user.getId()
        );

        return user.getId();
    }
}

