package NarayanGroup.example.E_Commerce.DTO.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDTO {

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity should be greater than zero")
    private Integer quantity;

}