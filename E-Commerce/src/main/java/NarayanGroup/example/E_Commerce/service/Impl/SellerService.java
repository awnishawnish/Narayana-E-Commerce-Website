package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Category;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.SellerStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.ICategoryRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.ISellerRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SellerService implements ISellerService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SellerService.class);

    private final ISellerRepository sellerRepository;
    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;

    @Override
    @Transactional
    public Seller registerSeller(String email, SellerRegistrationRequestDTO request) {
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException.UserNotFoundException(
                    "User not found with email: " + email
            );
        }

        if (sellerRepository.existsByUserIdAndIsDeletedFalse(user.getId())) {
            throw new IllegalArgumentException(ErrorConstants.SELLER_PROFILE_ALREADY_EXISTS);
        }

        Category category = categoryRepository
                .findByIdAndIsDeletedFalse(request.getProductCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.PRODUCT_CATEGORY_NOT_FOUND));

        if (category.isCustom() && category.getSeller() != null) {
            throw new IllegalArgumentException(ErrorConstants.PREDEFINED_CATEGORY_REQUIRED_FOR_SELLER);
        }

        Seller seller = Seller.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .mobileNumber(request.getMobileNumber())
                .mobileVerified(false)
                .businessType(request.getBusinessType())
                .businessAddress(request.getBusinessAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .productCategory(category)
                .status(SellerStatus.ACTIVE)
                .isDeleted(false)
                .build();

        return sellerRepository.save(seller);
    }

    @Override
    @Transactional(readOnly = true)
    public Seller getSellerByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException.UserNotFoundException(
                    "User not found with email: " + email
            );
        }
        return getSellerByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Seller getSellerByUserId(Long userId) {
        return sellerRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.SELLER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Seller getActiveSellerByUserId(Long userId) {
        return sellerRepository
                .findByUserIdAndStatusAndIsDeletedFalse(userId, SellerStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.ACTIVE_SELLER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActiveSeller(Long userId) {
        return sellerRepository
                .findByUserIdAndStatusAndIsDeletedFalse(userId, SellerStatus.ACTIVE)
                .isPresent();
    }
}
