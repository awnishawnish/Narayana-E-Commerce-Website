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
public class CheckoutRequestDTO {

    @NotNull(message = "Address id is required")
    @Positive(message = "Address id must be positive")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;
}