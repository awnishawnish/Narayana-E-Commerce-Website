package NarayanGroup.example.E_Commerce.facade;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;

public interface IInventoryFacade {

    Inventory findByProductId(Long productId);

    Inventory reserveStock(
            Long productId,
            Long quantity
    );

    Inventory save(Inventory inventory);
}