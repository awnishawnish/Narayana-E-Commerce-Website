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

    Optional<Order> findByIdAndIsDeletedFalse(Long orderId);

    List<Order> findByStatusAndCreatedAtBeforeAndIsDeletedFalse(
            NarayanGroup.example.E_Commerce.model.Enum.OrderStatus status,
            java.time.LocalDateTime before
    );

    boolean existsByOrderNumber(String orderNumber);
}