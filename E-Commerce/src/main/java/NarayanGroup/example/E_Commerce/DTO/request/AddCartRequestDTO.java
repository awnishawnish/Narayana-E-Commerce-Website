package NarayanGroup.example.E_Commerce.DTO.request;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequestDTO {
    @NotEmpty(message = ErrorConstants.PRODUCTS_LIST_EMPTY)
    @Valid
    private List<AddCartItemRequestDTO> products;
}