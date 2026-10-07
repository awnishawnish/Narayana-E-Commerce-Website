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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CartController.class);

    private final ICartFacade iCartFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addToCart(Long userId,
            AddCartRequestDTO request
    ) {

        logger.info("addToCart called for userId={}", userId);

        ResponseMessageUtilityDTO response =
                iCartFacade.addToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getCart(Long userId) {

        logger.info("getCart called for userId={}", userId);

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

        logger.info("updateQuantity called for userId={} cartItemId={} delta={}", userId, cartItemId, request.getQuantity());

        ResponseMessageUtilityDTO response =
                iCartFacade.updateQuantity(userId,cartItemId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> removeItem(Long userId,
            Long cartItemId
    ) {

        logger.info("removeItem called for userId={} cartItemId={}", userId, cartItemId);

        ResponseMessageUtilityDTO response =
                iCartFacade.removeItem(userId,cartItemId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> clearCart(Long userId) {

        logger.info("clearCart called for userId={}", userId);

        ResponseMessageUtilityDTO response =
                iCartFacade.clearCart(userId);

        return ResponseEntity.ok(response);
    }

}