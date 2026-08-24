package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity getByEmailAndPassword(String email, String password);
}