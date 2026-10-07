package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.DTO.response.SellerResponseDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.facade.ISellerFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SellerFacade implements ISellerFacade {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SellerFacade.class);

    private final ISellerService sellerService;

    @Override
    public ResponseMessageUtilityDTO registerSeller(String email, SellerRegistrationRequestDTO request) {
        Seller seller = sellerService.registerSeller(email, request);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.SELLER_REGISTERED_SUCCESSFULLY)
                .data(toResponse(seller))
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO getProfile(String email) {
        Seller seller = sellerService.getSellerByEmail(email);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SELLER_PROFILE_FETCHED_SUCCESSFULLY)
                .data(toResponse(seller))
                .build();
    }

    private SellerResponseDTO toResponse(Seller seller) {
        return SellerResponseDTO.builder()
                .id(seller.getId())
                .userId(seller.getUser().getId())
                .businessName(seller.getBusinessName())
                .mobileNumber(seller.getMobileNumber())
                .mobileVerified(seller.isMobileVerified())
                .businessType(seller.getBusinessType())
                .businessAddress(seller.getBusinessAddress())
                .productCategoryId(seller.getProductCategory() == null ? null : seller.getProductCategory().getId())
                .productCategoryName(seller.getProductCategory() == null ? null : seller.getProductCategory().getName())
                .status(seller.getStatus())
                .build();
    }
}
