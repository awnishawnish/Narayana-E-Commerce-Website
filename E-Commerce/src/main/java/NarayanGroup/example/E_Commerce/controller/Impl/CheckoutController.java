package NarayanGroup.example.E_Commerce.controller.Impl;
import NarayanGroup.example.E_Commerce.DTO.request.CheckoutRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.ICheckoutController;
import NarayanGroup.example.E_Commerce.facade.ICheckoutFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CheckoutController
        implements ICheckoutController {

    private final ICheckoutFacade checkoutFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> checkout(
            Long userId,
            CheckoutRequestDTO request) {

        return ResponseEntity
                .status(201)
                .body(
                        checkoutFacade.checkout(
                                userId,
                                request
                        )
                );
    }
}
