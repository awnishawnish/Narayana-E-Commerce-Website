package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.UserRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;

import java.util.List;

public interface IUserService {
    UserEntity saveUserDetails(UserRequestDTO userRequestDTO);

    boolean generateOtp(String email);

    boolean isOtpValid(String email, String otp);

    void resetPassword(String otp, String email, String password);

    UserEntity getUserByEmail(String email);

    List<UserEntity> getAllDetails();

    UserEntity getUserById(Long userId);

    UserEntity getByEmailAndPassword(String email, String password);
}
