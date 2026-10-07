package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.DTO.request.SellerRegistrationRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ActiveRoleResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.facade.ISellerFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@AllArgsConstructor
public class SellerController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SellerController.class);

    private final ISellerFacade sellerFacade;
    private final ISellerService sellerService;
    private final IUserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageUtilityDTO> registerSeller(
            @Valid @RequestBody SellerRegistrationRequestDTO request,
            Authentication authentication
    ) {
        logger.info("registerSeller called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.status(201)
                .body(sellerFacade.registerSeller(authentication.getName(), request));
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseMessageUtilityDTO> getProfile(Authentication authentication) {
        logger.info("getProfile called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.ok(sellerFacade.getProfile(authentication.getName()));
    }

    @PostMapping("/switch")
    public ResponseEntity<ResponseMessageUtilityDTO> switchToVendor(
            Authentication authentication,
            HttpServletResponse response
    ) {
        logger.info("switchToVendor called by user={}", authentication != null ? authentication.getName() : "anonymous");
        UserEntity user = getUser(authentication);
        UserDetails userDetails = toUserDetails(user);
        sellerService.getActiveSellerByUserId(user.getId());

        String accessToken = jwtUtil.generateAccessToken(userDetails, Role.VENDOR);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, Role.VENDOR);
        addRefreshCookie(response, refreshToken);

        ActiveRoleResponseDTO roleResponse = ActiveRoleResponseDTO.builder()
                .accessToken(accessToken)
                .activeRole(Role.VENDOR)
                .seller(true)
                .build();

        logger.info("switchToVendor succeeded for user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.ok()
                .body(ResponseMessageUtilityDTO.builder()
                        .status(CommonConstants.SUCCESS)
                        .httpStatus(200)
                        .message(CommonConstants.VENDOR_MODE_ACTIVATED_SUCCESSFULLY)
                        .data(roleResponse)
                        .build());
    }

    @PostMapping("/switch-to-user")
    public ResponseEntity<ResponseMessageUtilityDTO> switchToUser(
            Authentication authentication,
            HttpServletResponse response
    ) {
        logger.info("switchToUser called by user={}", authentication != null ? authentication.getName() : "anonymous");
        UserEntity user = getUser(authentication);
        UserDetails userDetails = toUserDetails(user);

        String accessToken = jwtUtil.generateAccessToken(userDetails, Role.USER);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, Role.USER);
        addRefreshCookie(response, refreshToken);

        ActiveRoleResponseDTO roleResponse = ActiveRoleResponseDTO.builder()
                .accessToken(accessToken)
                .activeRole(Role.USER)
                .seller(true)
                .build();

        logger.info("switchToUser succeeded for user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.ok()
                .body(ResponseMessageUtilityDTO.builder()
                        .status(CommonConstants.SUCCESS)
                        .httpStatus(200)
                        .message(CommonConstants.USER_MODE_ACTIVATED_SUCCESSFULLY)
                        .data(roleResponse)
                        .build());
    }

    private UserEntity getUser(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException(ErrorConstants.USER_NOT_FOUND);
        }
        return user;
    }

    private UserDetails toUserDetails(UserEntity user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
