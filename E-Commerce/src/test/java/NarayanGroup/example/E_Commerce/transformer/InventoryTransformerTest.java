package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTransformerTest {
    @Test void toEntity_shouldMapQuantityAndProduct() {
        Product product = Product.builder().id(1L).build();
        ProductRequestDTO request = ProductRequestDTO.builder().quantity(5).build();
        var result = new InventoryTransformer().toEntity(product, request);
        assertSame(product, result.getProduct());
        assertEquals(5L, result.getAvailableQuantity());
        assertEquals(0L, result.getReservedQuantity());
    }
}
