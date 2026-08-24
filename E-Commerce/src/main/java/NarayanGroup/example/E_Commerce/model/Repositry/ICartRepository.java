package NarayanGroup.example.E_Commerce.model.Repositry;


import NarayanGroup.example.E_Commerce.model.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndIsDeletedFalse(Long userId);

    boolean existsByUserIdAndIsDeletedFalse(Long userId);

    Cart findByUserId(Long userId);
}
