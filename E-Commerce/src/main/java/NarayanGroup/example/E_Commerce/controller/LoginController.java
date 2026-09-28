package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.LoginResponseDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import NarayanGroup.example.E_Commerce.service.Impl.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final IUserFacade iUserFacade;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserRepository userRepositry;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ResponseMessageUtilityDTO> authenticateUser(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        logger.info("Login attempt for email: {}", loginDTO.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        UserDetails userDetails = loginService.loadUserByUsername(loginDTO.getEmail());

        if (userDetails == null) {
            logger.error("User not found with email: {}", loginDTO.getEmail());
            throw new CustomException.UserNotFoundException(ErrorConstants.USER_NOT_FOUND_WITH_EMAIL + loginDTO.getEmail());
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        ResponseMessageUtilityDTO responseMessageUtilityDTO = iUserFacade.getUserByEmail(loginDTO.getEmail());
UserEntity user = (UserEntity) responseMessageUtilityDTO.getData();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();


        ResponseMessageUtilityDTO responseDTO = new ResponseMessageUtilityDTO(CommonConstants.PAYMENT_STATUS_SUCCESS, HttpStatus.OK.value(),
                "Login successful", "BID", loginResponseDTO);

        logger.info("Login successful for user: {}", loginDTO.getEmail());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        logger.info("Refresh token request received");
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            logger.warn("Refresh token missing in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorConstants.REFRESH_TOKEN_MISSING);
        }

        try {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            UserDetails userDetails = loginService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateAccessToken(userDetails);
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                logger.info("Refresh token validated, new access token issued for user: {}", username);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Invalid refresh token for user: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorConstants.INVALID_REFRESH_TOKEN);
            }
        } catch (Exception e) {
            logger.error("Error validating refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorConstants.INVALID_REFRESH_TOKEN);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        logger.info("Logout request received");
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        logger.info("User logged out successfully, refresh token cookie cleared");

        return ResponseEntity.ok().build();
    }
}