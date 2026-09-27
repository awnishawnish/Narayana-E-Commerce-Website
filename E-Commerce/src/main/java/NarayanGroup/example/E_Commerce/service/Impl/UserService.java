package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.service.IUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static NarayanGroup.example.E_Commerce.model.Enum.Role.USER;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final JavaMailSender mailSender;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public UserEntity saveUserDetails(UserRequestDTO userRequestDTO) {
        logger.info("Attempting to save user details for email: {}", userRequestDTO.getEmail());
        UserEntity existUserEntity = userRepository.findByEmail(userRequestDTO.getEmail());
        if (existUserEntity != null) {
            logger.warn("User already exists with email: {}", userRequestDTO.getEmail());
            return null;
        }
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .name(userRequestDTO.getName())
                .email(userRequestDTO.getEmail())
                .password(encodedPassword)
                .role(USER)
                .build();
        UserEntity saved = userRepository.save(userEntity);
        logger.info("User saved successfully with ID: {}", saved.getId());
        return saved;
    }

    public boolean generateOtp(String email) {
        logger.info("Generating OTP for email: {}", email);
        if (email == null || email.isEmpty()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new CustomException.UserNotFoundException("No users found.");
        }
        String otp = String.format("%06d", new Random().nextInt(999999));
        String redisKey = "otp:" + email;
        redisTemplate.opsForValue().set(redisKey, otp, 10, TimeUnit.MINUTES);
        logger.debug("OTP {} stored in Redis for email: {}", otp, email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nThis code is valid for 10 minutes.");
        try {
            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", email);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            return false;
        }
    }

    public boolean isOtpValid(String email, String otp) {
        logger.info("Validating OTP for email: {}", email);
        String redisKey = "otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(redisKey);
            logger.info("OTP validated successfully for email: {}", email);
            return true;
        }
        logger.warn("Invalid or expired OTP for email: {}", email);
        return false;
    }

    public void resetPassword(String otp, String email, String password) {
        logger.info("Resetting password for email: {}", email);
            UserEntity userEntity = userRepository.findByEmail(email);

            if (userEntity == null) {
                logger.error("User not found with email: {}", email);
                throw new CustomException.UserNotFoundException("No users found.");
            }
            String encodedPassword = passwordEncoder.encode(password);
            userEntity.setPassword(encodedPassword);
            userRepository.save(userEntity);
            logger.info("Password reset successfully for email: {}", email);

    }

    public List<UserEntity> getAllDetails() {
        logger.info("Fetching all user details");
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.warn("No users found in database");
            throw new CustomException.UserNotFoundException("No users found.");
        }
        logger.info("Fetched {} users from database", users.size());
        return users;
    }

    public UserEntity getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new CustomException.UserNotFoundException("User not found with email: " + email);
        }
        logger.info("User fetched successfully with email: {}", email);
        return user;
    }

    public UserEntity getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new CustomException.UserNotFoundException("User not found with id: " + userId);
                });
    }

    public UserEntity getByEmailAndPassword(String email, String password) {
return userRepository.getByEmailAndPassword(email,password);
    }
}

