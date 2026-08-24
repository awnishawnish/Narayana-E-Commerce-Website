package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.ICartController;
import NarayanGroup.example.E_Commerce.facade.ICartFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CartController implements ICartController {

    private final ICartFacade iCartFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addToCart(Long userId,
            AddCartRequestDTO request
    ) {

        ResponseMessageUtilityDTO response =
                iCartFacade.addToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getCart(Long userId) {

        ResponseMessageUtilityDTO response =
                iCartFacade.getCart(userId);

        return ResponseEntity.ok(response);
    }



    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> updateQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDTO request
    ) {

        ResponseMessageUtilityDTO response =
                iCartFacade.updateQuantity(userId,cartItemId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> removeItem(Long userId,
            Long cartItemId
    ) {

        ResponseMessageUtilityDTO response =
                iCartFacade.removeItem(userId,cartItemId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> clearCart(Long userId) {

        ResponseMessageUtilityDTO response =
                iCartFacade.clearCart(userId);

        return ResponseEntity.ok(response);
    }

}