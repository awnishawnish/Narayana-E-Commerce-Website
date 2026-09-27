package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Repositry.IInventoryRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock IInventoryRepository inventoryRepository;
    @Mock IProductRepository productRepository;
    InventoryService service;

    @BeforeEach void setUp() { service = new InventoryService(inventoryRepository, productRepository); }

    @Test void reserveStock_shouldMoveQuantityToReserved() {
        Inventory inventory = Inventory.builder().availableQuantity(10L).reservedQuantity(2L).build();
        when(inventoryRepository.findForUpdate(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        service.reserveStock(1L, 3L);

        assertEquals(7L, inventory.getAvailableQuantity());
        assertEquals(5L, inventory.getReservedQuantity());
    }

    @Test void reserveStock_shouldRejectInsufficientStock() {
        Inventory inventory = Inventory.builder().availableQuantity(2L).reservedQuantity(0L).build();
        when(inventoryRepository.findForUpdate(1L)).thenReturn(Optional.of(inventory));
        assertThrows(RuntimeException.class, () -> service.reserveStock(1L, 3L));
    }

    @Test void finalizeReservation_shouldDecreaseProductAndReserved() {
        Product product = Product.builder().id(1L).quantity(10).build();
        Inventory inventory = Inventory.builder().product(product).availableQuantity(5L).reservedQuantity(3L).build();
        when(inventoryRepository.findForUpdate(1L)).thenReturn(Optional.of(inventory));
        when(productRepository.save(product)).thenReturn(product);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        service.finalizeReservation(1L, 2L);

        assertEquals(8, product.getQuantity());
        assertEquals(1L, inventory.getReservedQuantity());
        verify(productRepository).save(product);
        verify(inventoryRepository).save(inventory);
    }

    @Test void releaseReservation_shouldReturnQuantityToAvailable() {
        Inventory inventory = Inventory.builder().availableQuantity(5L).reservedQuantity(3L).build();
        when(inventoryRepository.findForUpdate(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        service.releaseReservation(1L, 2L);

        assertEquals(7L, inventory.getAvailableQuantity());
        assertEquals(1L, inventory.getReservedQuantity());
    }
}
