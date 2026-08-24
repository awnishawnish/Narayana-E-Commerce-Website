package NarayanGroup.example.E_Commerce.service.Impl;

import java.util.Collections;
import java.util.List;

import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Lazy
@AllArgsConstructor
public class LoginService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final IUserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);
        UserEntity userModel = userRepository.findByEmail(email);
        if (userModel == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        logger.info("User found: {}", userModel.getEmail());
        return buildUserForAuthentication(userModel);
    }

    private UserDetails buildUserForAuthentication(UserEntity user) {
        logger.debug("Building UserDetails for authentication, email: {}", user.getEmail());
        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
