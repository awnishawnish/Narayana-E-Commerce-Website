package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CheckoutResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.configuration.RazorpayConfig;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentItemEvent;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.facade.ICheckoutFacade;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CheckoutFacade implements ICheckoutFacade {
    private final ICartService cartService;
    private final IAddressService addressService;
    private final IInventoryService inventoryService;
    private final IUserService userService;
    private final IProductService productService;
    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IRazorpayService razorpayService;
    private final RazorpayConfig razorpayConfig;
    private final DomainEventProducer eventProducer;
    @Override
    @Transactional
    public ResponseMessageUtilityDTO checkout(
            Long userId,
            CheckoutRequestDTO request) {
        UserEntity user =
                userService.getUserById(userId);
        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod().trim().isEmpty()) {
            throw new CustomException.CheckoutException(
                    ErrorConstants.PAYMENT_METHOD_REQUIRED
            );
        }
        String paymentMethod =
                request.getPaymentMethod()
                        .trim()
                        .toUpperCase();

        if (!paymentMethod.equals(CommonConstants.COD)
                && !paymentMethod.equals(CommonConstants.RAZORPAY)) {

            throw new CustomException.CheckoutException(
                    ErrorConstants.INVALID_PAYMENT_METHOD +
                            ErrorConstants.SUPPORTED_PAYMENT_METHODS
            );
        }


        /*
         * ============================================================
         * 3. GET USER CART
         * ============================================================
         */

        Cart cart =
                cartService.findByUserid(userId);

        if (cart == null) {

            throw new CustomException.CartNotFoundException(
                    ErrorConstants.CART_NOT_FOUND
            );
        }


        /*
         * ============================================================
         * 4. GET ACTIVE CART ITEMS
         * ============================================================
         */

        List<CartItem> activeCartItems =
                cart.getCartItems()
                        .stream()
                        .filter(item -> !item.isDeleted())
                        .collect(Collectors.toList());

        if (activeCartItems.isEmpty()) {

            throw new CustomException.CheckoutException(
                    ErrorConstants.CART_EMPTY
            );
        }


        /*
         * ============================================================
         * 5. SELECT ITEMS FOR CHECKOUT
         * ============================================================
         *
         * If cartItemIds are provided:
         *
         *     Buy Now
         *     Buy this now
         *     Selected cart items
         *
         * Example:
         *
         *     [12]
         *
         * If cartItemIds are null/empty:
         *
         *     Checkout entire cart
         */

        List<Long> requestedCartItemIds =
                request.getCartItemIds();

        List<CartItem> checkoutItems;


        if (requestedCartItemIds == null
                || requestedCartItemIds.isEmpty()) {

            /*
             * Checkout complete cart
             */

            checkoutItems = activeCartItems;

        } else {

            /*
             * Convert requested IDs into Set
             */

            Set<Long> requestedIds =
                    new HashSet<>(requestedCartItemIds);


            /*
             * Get IDs of active cart items
             */

            Set<Long> activeCartItemIds =
                    activeCartItems
                            .stream()
                            .map(CartItem::getId)
                            .collect(Collectors.toSet());


            /*
             * Make sure every requested cart item
             * actually belongs to this user's cart.
             */

            if (!activeCartItemIds.containsAll(requestedIds)) {

                throw new CustomException.CheckoutException(
                        ErrorConstants.INVALID_SELECTED_CART_ITEMS
                );
            }


            /*
             * Select requested items only
             */

            checkoutItems =
                    activeCartItems
                            .stream()
                            .filter(item ->
                                    requestedIds.contains(item.getId()))
                            .collect(Collectors.toList());
        }


        if (checkoutItems.isEmpty()) {

            throw new CustomException.CheckoutException(
                    ErrorConstants.NO_ITEMS_SELECTED_FOR_CHECKOUT
            );
        }


        /*
         * ============================================================
         * 6. VALIDATE ADDRESS
         * ============================================================
         */

        Address address =
                addressService.findActiveAddress(
                        request.getAddressId(),
                        userId
                );


        /*
         * ============================================================
         * 7. CALCULATE PRICE FROM DATABASE
         * ============================================================
         *
         * NEVER trust frontend price.
         *
         * Product price is always taken from database.
         */

        BigDecimal subtotal =
                BigDecimal.ZERO;

        String currency = null;


        for (CartItem cartItem : checkoutItems) {

            Product product =
                    productService.getProductById(
                            cartItem.getProduct().getId()
                    );


            /*
             * First product decides currency
             */

            if (currency == null) {

                currency = product.getCurrency();
            }


            /*
             * Make sure all products have same currency
             */

            if (!currency.equals(product.getCurrency())) {

                throw new CustomException.CheckoutException(
                        ErrorConstants.DIFFERENT_CURRENCIES
                );
            }


            /*
             * Calculate item total
             */

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );


            subtotal =
                    subtotal.add(itemTotal);


            /*
             * Reserve inventory
             */

            inventoryService.reserveStock(
                    product.getId(),
                    cartItem.getQuantity().longValue()
            );
        }


        /*
         * ============================================================
         * 8. CALCULATE CHARGES
         * ============================================================
         *
         * Currently:
         *
         * Tax       = 0
         * Delivery  = 0
         * Discount  = 0
         *
         * You can make these dynamic later.
         */

        BigDecimal tax =
                BigDecimal.ZERO;

        BigDecimal deliveryCharge =
                BigDecimal.ZERO;

        BigDecimal discount =
                BigDecimal.ZERO;


        BigDecimal totalAmount =
                subtotal
                        .add(tax)
                        .add(deliveryCharge)
                        .subtract(discount);


        /*
         * ============================================================
         * 9. DETERMINE INITIAL ORDER STATUS
         * ============================================================
         *
         * COD:
         *
         *     CONFIRMED
         *
         * Razorpay:
         *
         *     PENDING_PAYMENT
         */

        OrderStatus initialOrderStatus;


        if (CommonConstants.COD.equals(paymentMethod)) {

            initialOrderStatus =
                    OrderStatus.CONFIRMED;

        } else {

            initialOrderStatus =
                    OrderStatus.PENDING_PAYMENT;
        }


        /*
         * ============================================================
         * 10. CREATE LOCAL ORDER
         * ============================================================
         */

        Order order =
                Order.builder()
                        .orderNumber(
                                generateOrderNumber()
                        )
                        .user(user)

                        .status(initialOrderStatus)

                        /*
                         * COD payment will be collected later.
                         *
                         * Razorpay payment is also initially pending.
                         */
                        .paymentStatus(CommonConstants.PAYMENT_STATUS_PENDING)

                        .paymentMethod(paymentMethod)

                        .subtotal(subtotal)
                        .tax(tax)
                        .deliveryCharge(deliveryCharge)
                        .discount(discount)
                        .totalAmount(totalAmount)
                        .currency(currency)

                        /*
                         * Shipping address snapshot
                         */

                        .shippingFullName(
                                address.getFullName()
                        )

                        .shippingPhone(
                                address.getPhone()
                        )

                        .shippingAddressLine1(
                                address.getAddressLine1()
                        )

                        .shippingAddressLine2(
                                address.getAddressLine2()
                        )

                        .shippingCity(
                                address.getCity()
                        )

                        .shippingState(
                                address.getState()
                        )

                        .shippingPincode(
                                address.getPincode()
                        )

                        .isDeleted(false)

                        .build();


        /*
         * ============================================================
         * 11. CREATE ORDER ITEMS
         * ============================================================
         */

        for (CartItem cartItem : checkoutItems) {

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

                            .sourceCartItemId(cartItem.getId())

                            .product(product)

                            .productTitle(
                                    product.getTitle()
                            )

                            .quantity(
                                    cartItem.getQuantity()
                                            .longValue()
                            )

                            .unitPrice(
                                    product.getPrice()
                            )

                            .totalPrice(itemTotal)

                            .currency(
                                    product.getCurrency()
                            )

                            .isDeleted(false)

                            .build();


            order.getOrderItems()
                    .add(orderItem);
        }


        /*
         * ============================================================
         * 12. SAVE LOCAL ORDER
         * ============================================================
         */

        Order savedOrder =
                orderRepository.save(order);


        /*
         * ============================================================
         * 13. CREATE RAZORPAY ORDER ONLY FOR RAZORPAY
         * ============================================================
         *
         * COD:
         *
         *     No Razorpay order
         *
         * RAZORPAY:
         *
         *     Create Razorpay order
         */

        String razorpayOrderId = null;


        if (CommonConstants.RAZORPAY.equals(paymentMethod)) {

            try {

                razorpayOrderId =
                        razorpayService.createOrder(
                                totalAmount,
                                currency,
                                savedOrder.getOrderNumber()
                        );

            } catch (Exception e) {

                throw new CustomException.CheckoutException(
                        ErrorConstants.RAZORPAY_ORDER_CREATION_FAILED
                );
            }
        }


        /*
         * ============================================================
         * 14. CREATE PAYMENT RECORD
         * ============================================================
         *
         * COD:
         *
         *     gatewayOrderId = null
         *     status = PENDING
         *
         * RAZORPAY:
         *
         *     gatewayOrderId = Razorpay order ID
         *     status = PENDING
         */

        Payment payment =
                Payment.builder()

                        .paymentId(
                                CommonConstants.PAYMENT_ID_PREFIX +
                                        UUID.randomUUID()
                                                .toString()
                                                .replace("-", "")
                        )

                        .order(savedOrder)

                        .gatewayOrderId(
                                razorpayOrderId
                        )

                        .amount(
                                totalAmount
                        )

                        .currency(
                                currency
                        )

                        .paymentMethod(
                                paymentMethod
                        )

                        .status(
                                CommonConstants.PAYMENT_STATUS_PENDING
                        )

                        .isDeleted(false)

                        .build();


        paymentRepository.save(payment);


        /*
         * ============================================================
         * 15. COD PROCESSING
         * ============================================================
         *
         * COD order is already confirmed.
         *
         * Therefore remove the purchased cart items.
         *
         * IMPORTANT:
         *
         * We remove only checkoutItems,
         * not the complete cart.
         */

        if (CommonConstants.COD.equals(paymentMethod)) {

            for (CartItem cartItem : checkoutItems) {

                cartService.removeItem(
                        userId,
                        cartItem.getId()
                );
            }

            OrderConfirmedEvent orderConfirmedEvent = OrderConfirmedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .orderId(savedOrder.getId())
                            .orderNumber(savedOrder.getOrderNumber())
                            .paymentMethod(paymentMethod)
                            .amount(savedOrder.getTotalAmount())
                            .currency(savedOrder.getCurrency())
                            .userId(user.getId())
                            .userEmail(user.getEmail())
                            .items(checkoutItems.stream()
                                    .map(item -> PaymentItemEvent.builder()
                                            .productId(item.getProduct().getId())
                                            .quantity(item.getQuantity().longValue())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();
            publishAfterCommit(() -> eventProducer.publishOrderConfirmed(orderConfirmedEvent));
        }


        /*
         * ============================================================
         * 16. PREPARE RESPONSE
         * ============================================================
         *
         * COD:
         *
         *     razorpayOrderId = null
         *     razorpayKeyId = null
         *
         * RAZORPAY:
         *
         *     razorpayOrderId = actual Razorpay order
         *     razorpayKeyId = public Razorpay key
         */

        CheckoutResponseDTO response =
                CheckoutResponseDTO.builder()

                        .orderId(
                                savedOrder.getId()
                        )

                        .orderNumber(
                                savedOrder.getOrderNumber()
                        )

                        .razorpayOrderId(
                                razorpayOrderId
                        )

                        .razorpayKeyId(
                                CommonConstants.RAZORPAY.equals(paymentMethod)
                                        ? razorpayConfig.getKeyId()
                                        : null
                        )

                        .status(
                                savedOrder
                                        .getStatus()
                                        .name()
                        )

                        .paymentStatus(
                                savedOrder
                                        .getPaymentStatus()
                        )

                        .totalAmount(
                                savedOrder.getTotalAmount()
                        )

                        .currency(
                                savedOrder.getCurrency()
                        )

                        .build();


        /*
         * ============================================================
         * 17. RETURN RESPONSE
         * ============================================================
         */

        String message;

        if (CommonConstants.COD.equals(paymentMethod)) {

            message =
                    CommonConstants.ORDER_PLACED_SUCCESSFULLY;

        } else {

            message =
                    CommonConstants.CHECKOUT_INITIATED_SUCCESSFULLY;
        }


        return ResponseMessageUtilityDTO.builder()

                .status(CommonConstants.SUCCESS)

                .httpStatus(201)

                .message(message)

                .data(response)

                .build();
    }


    private void publishAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    /*
     * ================================================================
     * GENERATE ORDER NUMBER
     * ================================================================
     */

    private String generateOrderNumber() {

        return CommonConstants.ORDER_NUMBER_PREFIX +
                System.currentTimeMillis() +
                "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();
    }
}