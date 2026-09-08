package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.facade.IInventoryFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InventoryFacade implements IInventoryFacade {

    private final IInventoryService inventoryService;

    @Override
    public Inventory findByProductId(Long productId) {

        return inventoryService.findByProductId(productId);
    }

    @Override
    public Inventory reserveStock(
            Long productId,
            Long quantity) {

        return inventoryService.reserveStock(
                productId,
                quantity
        );
    }

    @Override
    public Inventory save(Inventory inventory) {

        return inventoryService.save(inventory);
    }
}
