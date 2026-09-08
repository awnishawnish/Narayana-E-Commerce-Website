package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartIdAndIsDeletedFalse(Long cartId);

    Optional<CartItem> findByCartIdAndProductIdAndIsDeletedFalse(
            Long cartId,
            Long productId
    );

    Optional<CartItem> findByIdAndIsDeletedFalse(Long cartItemId);

    boolean existsByCartIdAndProductIdAndIsDeletedFalse(
            Long cartId,
            Long productId
    );
}