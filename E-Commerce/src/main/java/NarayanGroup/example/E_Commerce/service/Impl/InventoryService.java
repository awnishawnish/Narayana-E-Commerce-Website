package NarayanGroup.example.E_Commerce.service.Impl;


import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Repositry.IInventoryRepository;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class InventoryService
        implements IInventoryService {

    private final IInventoryRepository inventoryRepository;

    @Override
    public Inventory findByProductId(Long productId) {

        return inventoryRepository
                .findByProductIdAndIsDeletedFalse(productId)
                .orElseThrow(() ->
                        new CustomException.InventoryNotFoundException(
                                "Inventory not found for product: "
                                        + productId
                        )
                );
    }

    @Override
    @Transactional
    public Inventory reserveStock(
            Long productId,
            Long quantity) {

        Inventory inventory =
                inventoryRepository
                        .findByProductIdAndIsDeletedFalse(productId)
                        .orElseThrow(() ->
                                new CustomException.InventoryNotFoundException(
                                        "Inventory not found for product: "
                                                + productId
                                )
                        );

        if (inventory.getAvailableQuantity() < quantity) {

            throw new CustomException.InsufficientStockException(
                    "Insufficient stock for product: "
                            + productId
            );
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - quantity
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + quantity
        );

        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}