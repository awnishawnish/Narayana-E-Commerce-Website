package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository
        extends JpaRepository<Order, Long> {

    Optional<Order>
    findByIdAndUserIdAndIsDeletedFalse(
            Long orderId,
            Long userId
    );

    List<Order>
    findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long userId
    );

    boolean existsByOrderNumber(String orderNumber);
}