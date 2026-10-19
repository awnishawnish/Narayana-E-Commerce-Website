package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.security.JwtUtil;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerFacadeTest {
    private ISellerService sellerService;
    private IUserRepository userRepository;
    private JwtUtil jwtUtil;
    private SellerFacade facade;

    @BeforeEach void setUp() {
        sellerService = mock(ISellerService.class);
        userRepository = mock(IUserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        facade = new SellerFacade(sellerService, userRepository, jwtUtil);
    }

    @Test void switchToVendorRequiresExistingActiveSellerAndReturnsVendorRole() {
        UserEntity user = UserEntity.builder().id(10L).email("seller@test.com").password("encoded").role(Role.USER).build();
        when(userRepository.findByEmail("seller@test.com")).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(), eq(Role.VENDOR))).thenReturn("vendor-access");
        when(jwtUtil.generateRefreshToken(any(), eq(Role.VENDOR))).thenReturn("vendor-refresh");
        var response = facade.switchToVendor("seller@test.com", new MockHttpServletResponse());
        assertEquals(200, response.getHttpStatus());
        assertEquals(Role.VENDOR, ((NarayanGroup.example.E_Commerce.DTO.response.ActiveRoleResponseDTO) response.getData()).getActiveRole());
        verify(sellerService).getActiveSellerByUserId(10L);
    }

    @Test void switchToUserIssuesUserRoleTokens() {
        UserEntity user = UserEntity.builder().id(11L).email("user@test.com").password("encoded").role(Role.USER).build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(), eq(Role.USER))).thenReturn("user-access");
        when(jwtUtil.generateRefreshToken(any(), eq(Role.USER))).thenReturn("user-refresh");
        var response = facade.switchToUser("user@test.com", new MockHttpServletResponse());
        assertEquals(200, response.getHttpStatus());
        assertEquals(Role.USER, ((NarayanGroup.example.E_Commerce.DTO.response.ActiveRoleResponseDTO) response.getData()).getActiveRole());
        verifyNoInteractions(sellerService);
    }

    @Test void switchToUserRejectsUnknownUser() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> facade.switchToUser("missing@test.com", new MockHttpServletResponse()));
    }
}
