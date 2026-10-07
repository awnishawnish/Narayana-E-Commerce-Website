package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.model.Enum.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<Seller> findByUserIdAndStatusAndIsDeletedFalse(Long userId, SellerStatus status);

    boolean existsByUserIdAndIsDeletedFalse(Long userId);
}
