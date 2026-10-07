package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.controller.IInventoryController;
import NarayanGroup.example.E_Commerce.facade.IInventoryFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class InventoryController implements IInventoryController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InventoryController.class);

    private final IInventoryFacade inventoryFacade;


    public ResponseEntity<Inventory> findByProductId(
            @PathVariable Long productId) {

        logger.info("findByProductId called for productId={}", productId);

        return ResponseEntity.ok(
                inventoryFacade.findByProductId(productId)
        );
    }


    public ResponseEntity<Inventory> save(
            @RequestBody Inventory inventory) {

        // logger.info("save inventory called for productId={}", inventory.getProductId());

        Inventory savedInventory =
                inventoryFacade.save(inventory);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedInventory);
    }
}