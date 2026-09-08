package NarayanGroup.example.E_Commerce.controller;


import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public interface IInventoryController {

    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> findByProductId(
            @PathVariable Long productId);

    @PostMapping
    public ResponseEntity<Inventory> save(
            @RequestBody Inventory inventory);
}