package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.DTO.response.SellerResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ActiveRoleResponseDTO;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final IUserRepository userRepository;
    private final JwtUtil jwtUtil;

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

    @Override
    public ResponseMessageUtilityDTO switchToVendor(String email, HttpServletResponse response) {
        UserEntity user = getUser(email);
        sellerService.getActiveSellerByUserId(user.getId());
        UserDetails details = toUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(details, Role.VENDOR);
        String refreshToken = jwtUtil.generateRefreshToken(details, Role.VENDOR);
        addRefreshCookie(response, refreshToken);
        ActiveRoleResponseDTO roleResponse = ActiveRoleResponseDTO.builder()
                .accessToken(accessToken).activeRole(Role.VENDOR).seller(true).build();
        return roleResponse(CommonConstants.VENDOR_MODE_ACTIVATED_SUCCESSFULLY, roleResponse);
    }

    @Override
    public ResponseMessageUtilityDTO switchToUser(String email, HttpServletResponse response) {
        UserEntity user = getUser(email);
        UserDetails details = toUserDetails(user);
        String accessToken = jwtUtil.generateAccessToken(details, Role.USER);
        String refreshToken = jwtUtil.generateRefreshToken(details, Role.USER);
        addRefreshCookie(response, refreshToken);
        ActiveRoleResponseDTO roleResponse = ActiveRoleResponseDTO.builder()
                .accessToken(accessToken).activeRole(Role.USER).seller(true).build();
        return roleResponse(CommonConstants.USER_MODE_ACTIVATED_SUCCESSFULLY, roleResponse);
    }

    private UserEntity getUser(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }

    private UserDetails toUserDetails(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).authorities(user.getRole().name()).build();
    }

    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(false).path("/").maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseMessageUtilityDTO roleResponse(String message, ActiveRoleResponseDTO data) {
        return ResponseMessageUtilityDTO.builder().status(CommonConstants.SUCCESS)
                .httpStatus(200).message(message).data(data).build();
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
                .latitude(seller.getLatitude())
                .longitude(seller.getLongitude())
                .productCategoryId(seller.getProductCategory() == null ? null : seller.getProductCategory().getId())
                .productCategoryName(seller.getProductCategory() == null ? null : seller.getProductCategory().getName())
                .status(seller.getStatus())
                .build();
    }
}
