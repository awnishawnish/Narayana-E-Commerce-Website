package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.configuration.RazorpayConfig;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.kafka.producer.DomainEventProducer;
import NarayanGroup.example.E_Commerce.model.Entity.*;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IPaymentRepository;
import NarayanGroup.example.E_Commerce.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutFacadeTest {
    @Mock ICartService cartService;
    @Mock IAddressService addressService;
    @Mock IInventoryService inventoryService;
    @Mock IUserService userService;
    @Mock IProductService productService;
    @Mock IOrderRepository orderRepository;
    @Mock IPaymentRepository paymentRepository;
    @Mock IRazorpayService razorpayService;
    @Mock RazorpayConfig razorpayConfig;
    @Mock DomainEventProducer eventProducer;
    CheckoutFacade facade;

    @BeforeEach void setUp() {
        facade = new CheckoutFacade(cartService, addressService, inventoryService, userService, productService,
                orderRepository, paymentRepository, razorpayService, razorpayConfig, eventProducer);
    }

    @Test void checkout_shouldRejectInvalidPaymentMethod() {
        CheckoutRequestDTO request = CheckoutRequestDTO.builder().paymentMethod("CARD").build();
        when(userService.getUserById(1L)).thenReturn(UserEntity.builder().id(1L).build());
        assertThrows(RuntimeException.class, () -> facade.checkout(1L, request));
        verifyNoInteractions(cartService);
    }

    @Test void checkout_cod_shouldCreateOrderPaymentAndPublishEvent() {
        UserEntity user = UserEntity.builder().id(1L).email("user@test.com").build();
        Product product = Product.builder().id(10L).title("Phone").category("Mobile")
                .price(new BigDecimal("100.00")).currency("INR").build();
        CartItem item = CartItem.builder().id(20L).product(product).quantity(2).isDeleted(false).build();
        Cart cart = Cart.builder().id(2L).cartItems(Collections.singletonList(item)).build();
        Address address = Address.builder().id(3L).fullName("User").phone("9876543210")
                .addressLine1("Line 1").city("Noida").state("UP").pincode("201301").build();
        when(userService.getUserById(1L)).thenReturn(user);
        when(cartService.findByUserid(1L)).thenReturn(cart);
        when(addressService.findActiveAddress(3L, 1L)).thenReturn(address);
        when(productService.getProductById(10L)).thenReturn(product);
        when(inventoryService.reserveStock(10L, 2L)).thenReturn(Inventory.builder().build());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckoutRequestDTO request = CheckoutRequestDTO.builder().addressId(3L).paymentMethod(CommonConstants.COD).build();
        var response = facade.checkout(1L, request);

        assertEquals(CommonConstants.SUCCESS, response.getStatus());
        assertEquals(201, response.getHttpStatus());
        assertEquals(CommonConstants.ORDER_PLACED_SUCCESSFULLY, response.getMessage());
        verify(cartService).removeItem(1L, 20L);
        verify(eventProducer).publishOrderConfirmed(any());
        verifyNoInteractions(razorpayService);
    }
}
