package NarayanGroup.example.E_Commerce.model.Repositry;


import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;


import java.util.Optional;

public interface IInventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory>
    findByProductIdAndIsDeletedFalse(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.isDeleted = false")
    Optional<Inventory> findForUpdate(@Param("productId") Long productId);

}
