package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InventoryTransformer {

    public Inventory toEntity(Product p, ProductRequestDTO request) {
        return Inventory.builder()
                .product(p)
                .availableQuantity(Long.valueOf(request.getQuantity()))
                .reservedQuantity(0l)
                .build();
    }
}
