package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IUserFacade;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import NarayanGroup.example.E_Commerce.service.ILoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginFacadeTest {
    private IUserFacade userFacade;
    private AuthenticationManager authenticationManager;
    private ILoginService loginService;
    private JwtUtil jwtUtil;
    private LoginFacade facade;

    @BeforeEach void setUp() {
        userFacade = mock(IUserFacade.class);
        authenticationManager = mock(AuthenticationManager.class);
        loginService = mock(ILoginService.class);
        jwtUtil = mock(JwtUtil.class);
        facade = new LoginFacade(userFacade, authenticationManager, loginService, jwtUtil,
                mock(IUserRepository.class), mock(org.springframework.security.crypto.password.PasswordEncoder.class));
    }

    @Test void authenticateReturnsAccessTokenAndRefreshCookie() {
        LoginDTO dto = new LoginDTO(); dto.setEmail("u@test.com"); dto.setPassword("secret");
        var details = User.withUsername("u@test.com").password("encoded").authorities(Role.USER.name()).build();
        var user = UserEntity.builder().id(1L).name("User").email("u@test.com").role(Role.USER).build();
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.generateAccessToken(details)).thenReturn("access");
        when(jwtUtil.generateRefreshToken(details)).thenReturn("refresh");
        when(userFacade.getUserByEmail("u@test.com")).thenReturn(ResponseMessageUtilityDTO.builder().data(user).build());
        var response = facade.authenticateUser(dto, new MockHttpServletResponse());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("Set-Cookie"));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test void authenticateThrowsWhenUserDetailsMissing() {
        LoginDTO dto = new LoginDTO(); dto.setEmail("missing@test.com"); dto.setPassword("secret");
        when(loginService.loadUserByUsername(dto.getEmail())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> facade.authenticateUser(dto, new MockHttpServletResponse()));
    }

    @Test void refreshWithoutCookieReturnsUnauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED, facade.refreshToken(new MockHttpServletRequest()).getStatusCode());
    }

    @Test void refreshReturnsNewTokenForValidCookie() {
        var request = new MockHttpServletRequest(); request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "old"));
        var details = User.withUsername("u@test.com").password("p").authorities(Role.USER.name()).build();
        when(jwtUtil.getUsernameFromToken("old")).thenReturn("u@test.com");
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.validateToken("old", details)).thenReturn(true);
        when(jwtUtil.getActiveRoleFromToken("old")).thenReturn(null);
        when(jwtUtil.generateAccessToken(details)).thenReturn("new");
        var response = facade.refreshToken(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new", ((java.util.Map<?, ?>) response.getBody()).get("accessToken"));
    }

    @Test void refreshRejectsInvalidToken() {
        var request = new MockHttpServletRequest(); request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "old"));
        var details = User.withUsername("u@test.com").password("p").authorities(Role.USER.name()).build();
        when(jwtUtil.getUsernameFromToken("old")).thenReturn("u@test.com");
        when(loginService.loadUserByUsername("u@test.com")).thenReturn(details);
        when(jwtUtil.validateToken("old", details)).thenReturn(false);
        assertEquals(HttpStatus.UNAUTHORIZED, facade.refreshToken(request).getStatusCode());
    }

    @Test void refreshRejectsTokenParsingExceptions() {
        var request = new MockHttpServletRequest(); request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", "bad"));
        when(jwtUtil.getUsernameFromToken("bad")).thenThrow(new RuntimeException("bad token"));
        assertEquals(HttpStatus.UNAUTHORIZED, facade.refreshToken(request).getStatusCode());
    }

    @Test void logoutClearsRefreshCookie() {
        var response = facade.logout(new MockHttpServletResponse());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("Set-Cookie"));
    }
}
