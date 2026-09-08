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

    private final IInventoryFacade inventoryFacade;


    public ResponseEntity<Inventory> findByProductId(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryFacade.findByProductId(productId)
        );
    }


    public ResponseEntity<Inventory> save(
            @RequestBody Inventory inventory) {

        Inventory savedInventory =
                inventoryFacade.save(inventory);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedInventory);
    }
}