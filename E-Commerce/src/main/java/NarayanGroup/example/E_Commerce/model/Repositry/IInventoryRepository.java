package NarayanGroup.example.E_Commerce.model.Repositry;


import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface IInventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory>
    findByProductIdAndIsDeletedFalse(Long productId);

}
