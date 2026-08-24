package NarayanGroup.example.E_Commerce.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private List<AddCartItemRequestDTO> products;
}