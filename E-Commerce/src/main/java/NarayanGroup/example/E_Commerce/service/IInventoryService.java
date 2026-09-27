package NarayanGroup.example.E_Commerce.service;


import NarayanGroup.example.E_Commerce.model.Entity.Inventory;

public interface IInventoryService {

    Inventory findByProductId(Long productId);

    Inventory reserveStock(
            Long productId,
            Long quantity
    );

    Inventory save(Inventory inventory);

    Inventory finalizeReservation(Long productId, Long quantity);

    Inventory releaseReservation(Long productId, Long quantity);

    Inventory cancelReservationOrRestoreStock(Long productId, Long quantity);
}
