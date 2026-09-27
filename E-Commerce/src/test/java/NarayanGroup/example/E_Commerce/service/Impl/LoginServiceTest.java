package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Enum.Role;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {
    @Test void loadUser_shouldMapAuthorities() { IUserRepository repo=mock(IUserRepository.class); UserEntity u=UserEntity.builder().email("a@b.com").password("x").role(Role.USER).build(); when(repo.findByEmail("a@b.com")).thenReturn(u); var d=new LoginService(repo).loadUserByUsername("a@b.com"); assertEquals("a@b.com",d.getUsername()); assertEquals("x",d.getPassword()); assertTrue(d.getAuthorities().stream().anyMatch(a->a.getAuthority().equals(Role.USER.name()))); }
    @Test void missingUser_shouldThrow() { IUserRepository repo=mock(IUserRepository.class); when(repo.findByEmail("x")).thenReturn(null); assertThrows(UsernameNotFoundException.class,()->new LoginService(repo).loadUserByUsername("x")); }
}
