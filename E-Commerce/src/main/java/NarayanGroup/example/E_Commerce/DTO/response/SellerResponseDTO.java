package NarayanGroup.example.E_Commerce.DTO.response;

import NarayanGroup.example.E_Commerce.model.Enum.BusinessType;
import NarayanGroup.example.E_Commerce.model.Enum.SellerStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponseDTO {

    private Long id;
    private Long userId;
    private String businessName;
    private String mobileNumber;
    private boolean mobileVerified;
    private BusinessType businessType;
    private String businessAddress;
    private Double latitude;
    private Double longitude;
    private Long productCategoryId;
    private String productCategoryName;
    private SellerStatus status;
}
