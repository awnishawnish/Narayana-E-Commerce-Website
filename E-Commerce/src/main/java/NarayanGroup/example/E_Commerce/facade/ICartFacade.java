package NarayanGroup.example.E_Commerce.facade;
import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface ICartFacade {

    ResponseMessageUtilityDTO addToCart(Long userId, AddCartRequestDTO request);


    ResponseMessageUtilityDTO getCart(Long userId);

    ResponseMessageUtilityDTO updateQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDTO request
    );

    ResponseMessageUtilityDTO removeItem(
            Long userId, Long cartItemId
    );

    ResponseMessageUtilityDTO clearCart(Long userId);

}
