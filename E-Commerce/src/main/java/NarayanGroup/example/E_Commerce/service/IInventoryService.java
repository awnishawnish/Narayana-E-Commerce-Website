package NarayanGroup.example.E_Commerce.service;


import NarayanGroup.example.E_Commerce.model.Entity.Inventory;

public interface IInventoryService {

    Inventory findByProductId(Long productId);

    Inventory reserveStock(
            Long productId,
            Long quantity
    );

    Inventory save(Inventory inventory);
}
