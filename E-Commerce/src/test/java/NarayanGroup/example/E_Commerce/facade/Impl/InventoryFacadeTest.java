package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class InventoryFacadeTest {
    @Test void allOperations_shouldDelegate() {
        IInventoryService service=mock(IInventoryService.class); Inventory inventory=Inventory.builder().build();
        when(service.findByProductId(1L)).thenReturn(inventory); when(service.reserveStock(1L,2L)).thenReturn(inventory); when(service.save(inventory)).thenReturn(inventory);
        InventoryFacade facade=new InventoryFacade(service);
        assertSame(inventory,facade.findByProductId(1L)); assertSame(inventory,facade.reserveStock(1L,2L)); assertSame(inventory,facade.save(inventory));
        verify(service).findByProductId(1L); verify(service).reserveStock(1L,2L); verify(service).save(inventory);
    }
}
