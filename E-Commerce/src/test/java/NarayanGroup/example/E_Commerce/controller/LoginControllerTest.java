package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;

import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import NarayanGroup.example.E_Commerce.service.Impl.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {
    private final IUserFacade facade = mock(IUserFacade.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final LoginService loginService = mock(LoginService.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final IUserRepository repository = mock(IUserRepository.class);
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = mock(org.springframework.security.crypto.password.PasswordEncoder.class);

    private LoginController controller() {
        return new LoginController(facade, authenticationManager, loginService, jwtUtil, repository, passwordEncoder);
    }

    @Test
    void authenticateUser_shouldReturnTokensAndCookie() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("u@test.com");
        dto.setPassword("secret");
        var details = User.withUsername("u@test.com").password("encoded").roles("USER").build();
        var user = UserEntity.builder().id(1L).name("User").email("u@test.com").role(Role.USER).build();
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.generateAccessToken(details)).thenReturn("access");
        when(jwtUtil.generateRefreshToken(details)).thenReturn("refresh");
        when(facade.getUserByEmail("u@test.com")).thenReturn(ResponseMessageUtilityDTO.builder().data(user).build());

        var response = controller().authenticateUser(dto, new MockHttpServletResponse());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("access", response.getBody().getData() instanceof NarayanGroup.example.E_Commerce.DTO.response.LoginResponseDTO
                ? ((NarayanGroup.example.E_Commerce.DTO.response.LoginResponseDTO) response.getBody().getData()).getAccessToken() : null);
        assertNotNull(response.getHeaders().getFirst("Set-Cookie"));
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticateUser_shouldRejectWhenUserDetailsMissing() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("missing@test.com");
        dto.setPassword("secret");
        when(loginService.loadUserByUsername(dto.getEmail())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> controller().authenticateUser(dto, new MockHttpServletResponse()));
    }

    @Test
    void refreshToken_shouldRejectWhenCookieMissing() {
        var response = controller().refreshToken(new MockHttpServletRequest());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void refreshToken_shouldReturnNewTokenForValidCookie() {
        var request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "old"));
        var details = User.withUsername("u@test.com").password("p").roles("USER").build();
        when(jwtUtil.getUsernameFromToken("old")).thenReturn("u@test.com");
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.validateToken("old", details)).thenReturn(true);
        when(jwtUtil.generateAccessToken(details)).thenReturn("new");

        var response = controller().refreshToken(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new", ((java.util.Map<?, ?>) response.getBody()).get("accessToken"));
    }

    @Test
    void refreshToken_shouldRejectInvalidToken() {
        var request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "old"));
        var details = User.withUsername("u@test.com").password("p").roles("USER").build();
        when(jwtUtil.getUsernameFromToken("old")).thenReturn("u@test.com");
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.validateToken("old", details)).thenReturn(false);
        var response = controller().refreshToken(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void refreshToken_shouldRejectException() {
        var request = new MockHttpServletRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "old"));
        when(jwtUtil.getUsernameFromToken("old")).thenThrow(new RuntimeException("bad"));
        var response = controller().refreshToken(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void logout_shouldClearCookie() {
        var response = controller().logout(new MockHttpServletResponse());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("Set-Cookie"));
    }
}
