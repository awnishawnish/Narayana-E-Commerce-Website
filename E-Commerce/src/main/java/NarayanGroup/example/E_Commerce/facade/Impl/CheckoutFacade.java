package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CheckoutResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.facade.ICheckoutFacade;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.IAddressService;
import NarayanGroup.example.E_Commerce.service.ICartService;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import NarayanGroup.example.E_Commerce.service.IUserService;
import NarayanGroup.example.E_Commerce.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CheckoutFacade
        implements ICheckoutFacade {

    private final ICartService cartService;
    private final IAddressService addressService;
    private final IInventoryService inventoryService;
    private final IUserService userService;
    private final IProductService productService;

    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;

    @Override
    @Transactional
    public ResponseMessageUtilityDTO checkout(
            Long userId,
            CheckoutRequestDTO request) {

        UserEntity user =
                userService.getUserById(userId);

        Cart cart =
                cartService.findByUserid(userId);

        if (cart == null) {
            throw new CustomException.CartNotFoundException(
                    "Cart not found"
            );
        }

        /*
         * 3. Get active cart items
         */
        var cartItems = cart.getCartItems()
                .stream()
                .filter(item -> !item.isDeleted())
                .toList();

        if (cartItems.isEmpty()) {

            throw new CustomException.CheckoutException(
                    "Cart is empty"
            );
        }

        /*
         * 4. Validate address
         */
        Address address =
                addressService.findActiveAddress(
                        request.getAddressId(),
                        userId
                );

        /*
         * 5. Calculate current prices
         *
         * IMPORTANT:
         * Do not blindly trust priceAtAddition.
         *
         * Checkout should use the CURRENT product price.
         */
        BigDecimal subtotal =
                BigDecimal.ZERO;

        String currency = null;

        for (CartItem cartItem : cartItems) {

            Product product =
                    productService.getProductById(
                            cartItem.getProduct().getId()
                    );

            if (currency == null) {
                currency = product.getCurrency();
            }

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            subtotal =
                    subtotal.add(itemTotal);

            inventoryService.reserveStock(
                    product.getId(),
                    cartItem.getQuantity().longValue()
            );
        }

        /*
         * 7. Calculate charges
         *
         * Basic version:
         */
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal deliveryCharge = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal totalAmount =
                subtotal
                        .add(tax)
                        .add(deliveryCharge)
                        .subtract(discount);

        /*
         * 8. Create order
         */
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .paymentStatus("PENDING")
                .paymentMethod(request.getPaymentMethod())
                .subtotal(subtotal)
                .tax(tax)
                .deliveryCharge(deliveryCharge)
                .discount(discount)
                .totalAmount(totalAmount)
                .currency(currency)
                .shippingFullName(address.getFullName())
                .shippingPhone(address.getPhone())
                .shippingAddressLine1(address.getAddressLine1())
                .shippingAddressLine2(address.getAddressLine2())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .shippingPincode(address.getPincode())
                .isDeleted(false)
                .build();

        /*
         * 9. Create order items
         */
        for (CartItem cartItem : cartItems) {

            Product product =
                    productService.getProductById(
                            cartItem.getProduct().getId()
                    );

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .product(product)
                            .productTitle(product.getTitle())
                            .quantity(
                                    cartItem.getQuantity()
                                            .longValue()
                            )
                            .unitPrice(product.getPrice())
                            .totalPrice(itemTotal)
                            .currency(product.getCurrency())
                            .isDeleted(false)
                            .build();

            order.getOrderItems().add(orderItem);
        }

        Order savedOrder =
                orderRepository.save(order);

        /*
         * 10. Create payment record
         *
         * Actual payment gateway will be implemented later.
         */
        Payment payment =
                Payment.builder()
                        .paymentId(
                                "PAY-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .replace("-", "")
                        )
                        .order(savedOrder)
                        .amount(totalAmount)
                        .currency(currency)
                        .paymentMethod(
                                request.getPaymentMethod()
                        )
                        .status("PENDING")
                        .isDeleted(false)
                        .build();

        paymentRepository.save(payment);

        /*
         * 11. Clear active cart items
         *
         * We use soft delete.
         */
        cartItems.forEach(item ->
                item.setDeleted(true)
        );

        cart.setTotalItems(0);
        cart.setTotalAmount(BigDecimal.ZERO);

        /*
         * Cart is managed inside transaction,
         * so changes will be persisted.
         */

        CheckoutResponseDTO response =
                CheckoutResponseDTO.builder()
                        .orderId(savedOrder.getId())
                        .orderNumber(
                                savedOrder.getOrderNumber()
                        )
                        .status(
                                savedOrder.getStatus().name()
                        )
                        .paymentStatus(
                                savedOrder.getPaymentStatus()
                        )
                        .totalAmount(
                                savedOrder.getTotalAmount()
                        )
                        .currency(
                                savedOrder.getCurrency()
                        )
                        .build();

        return ResponseMessageUtilityDTO.builder()
                .status("Success")
                .httpStatus(201)
                .message(
                        "Checkout initiated successfully"
                )
                .data(response)
                .build();
    }

    private String generateOrderNumber() {

        return "ORD-" +
                System.currentTimeMillis() +
                "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();
    }
}