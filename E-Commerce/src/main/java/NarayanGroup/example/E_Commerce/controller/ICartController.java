package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.AddCartRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ResetPasswordRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UpdateCartItemRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userId/cart")
public interface ICartController {

    @PostMapping("{userId}/add")
    ResponseEntity<ResponseMessageUtilityDTO> addToCart(@PathVariable(name = "userId") Long userId,
                                                        @Valid @RequestBody AddCartRequestDTO request
    );

    @GetMapping("{userId}/get")
    ResponseEntity<ResponseMessageUtilityDTO> getCart(@PathVariable(name = "userId") Long userId);

    @PutMapping("{userId}/{cartItemId}")
    ResponseEntity<ResponseMessageUtilityDTO> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request
    );

    @DeleteMapping("{userId}/{cartItemId}")
    ResponseEntity<ResponseMessageUtilityDTO> removeItem(@PathVariable Long userId,
                                                         @PathVariable Long cartItemId
    );

    @DeleteMapping("{userId}/clear")
    ResponseEntity<ResponseMessageUtilityDTO> clearCart(@PathVariable Long userId);

}
