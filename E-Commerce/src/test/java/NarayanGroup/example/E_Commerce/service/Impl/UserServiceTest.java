package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {
    @Mock JavaMailSender mailSender;
    @Mock IUserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RedisTemplate<String,String> redisTemplate;
    @Mock ValueOperations<String,String> valueOperations;
    private UserService service() { return new UserService(mailSender,repository,passwordEncoder,redisTemplate); }

//    @Test void saveUserDetails_shouldReturnSavedUserAndRejectDuplicate() {
//        var req=UserRequestDTO.builder().name("User").email("u@test.com").password("raw").build(); var saved=UserEntity.builder().id(1L).build();
//        when(repository.findByEmail("u@test.com")).thenReturn(null); when(passwordEncoder.encode("raw")).thenReturn("encoded"); when(repository.save(any())).thenReturn(saved);
//        assertSame(saved,service().saveUserDetails(req)); verify(passwordEncoder).encode("raw");
//
//        when(repository.findByEmail("u@test.com")).thenReturn(saved);
//        assertNull(service().saveUserDetails(req)); verify(repository,never()).save(any());
//    }

    @Test void generateOtp_shouldRejectBlankAndMissingUser() {
        var s=service();
        assertThrows(IllegalArgumentException.class,()->s.generateOtp(""));
        when(repository.findByEmail("missing@test.com")).thenReturn(null);
        assertThrows(CustomException.UserNotFoundException.class,()->s.generateOtp("missing@test.com"));
    }

    @Test void generateOtp_shouldStoreAndSendAndReturnFalseWhenMailFails() {
        when(repository.findByEmail("u@test.com")).thenReturn(UserEntity.builder().email("u@test.com").build());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        var s=service();
        assertTrue(s.generateOtp("u@test.com"));
        verify(valueOperations).set(eq("otp:u@test.com"),anyString(),eq(10L),eq(java.util.concurrent.TimeUnit.MINUTES));
        verify(mailSender).send(any(SimpleMailMessage.class));

        doThrow(new RuntimeException("smtp")).when(mailSender).send(any(SimpleMailMessage.class));
        assertFalse(s.generateOtp("u@test.com"));
    }

    @Test void isOtpValid_shouldDeleteMatchingOtp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations); when(valueOperations.get("otp:u@test.com")).thenReturn("123456");
        assertTrue(service().isOtpValid("u@test.com","123456")); verify(redisTemplate).delete("otp:u@test.com");
        when(valueOperations.get("otp:u@test.com")).thenReturn("654321");
        assertFalse(service().isOtpValid("u@test.com","123456"));
    }

    @Test void resetPassword_shouldUpdateAndRejectMissingUser() {
        var user=UserEntity.builder().email("u@test.com").build(); when(repository.findByEmail("u@test.com")).thenReturn(user); when(passwordEncoder.encode("new")).thenReturn("encoded");
        service().resetPassword("otp","u@test.com","new"); assertEquals("encoded",user.getPassword()); verify(repository).save(user);
        when(repository.findByEmail("missing@test.com")).thenReturn(null);
        assertThrows(CustomException.UserNotFoundException.class,()->service().resetPassword("otp","missing@test.com","new"));
    }

    @Test void getAllDetails_shouldReturnUsersOrThrow() {
        var users=List.of(UserEntity.builder().id(1L).build()); when(repository.findAll()).thenReturn(users); assertSame(users,service().getAllDetails());
        when(repository.findAll()).thenReturn(List.of()); assertThrows(CustomException.UserNotFoundException.class,()->service().getAllDetails());
    }

    @Test void getUserByEmailAndId_shouldReturnOrThrow() {
        var user=UserEntity.builder().id(1L).email("u@test.com").build(); when(repository.findByEmail("u@test.com")).thenReturn(user); assertSame(user,service().getUserByEmail("u@test.com"));
        when(repository.findByEmail("missing@test.com")).thenReturn(null); assertThrows(CustomException.UserNotFoundException.class,()->service().getUserByEmail("missing@test.com"));
        when(repository.findById(1L)).thenReturn(Optional.of(user)); assertSame(user,service().getUserById(1L));
        when(repository.findById(2L)).thenReturn(Optional.empty()); assertThrows(CustomException.UserNotFoundException.class,()->service().getUserById(2L));
    }

    @Test void getByEmailAndPassword_shouldDelegate() {
        var user=UserEntity.builder().id(1L).build(); when(repository.getByEmailAndPassword("u","p")).thenReturn(user);
        assertSame(user,service().getByEmailAndPassword("u","p"));
    }
}
