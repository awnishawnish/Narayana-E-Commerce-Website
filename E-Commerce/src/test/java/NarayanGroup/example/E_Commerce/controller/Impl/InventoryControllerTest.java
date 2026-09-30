package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.facade.IInventoryFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryControllerTest {
    private final IInventoryFacade facade=mock(IInventoryFacade.class);
    private final InventoryController controller=new InventoryController(facade);

    @Test void findByProductId_shouldReturnOk() {
        Inventory inventory=Inventory.builder().build(); when(facade.findByProductId(10L)).thenReturn(inventory);
        var response=controller.findByProductId(10L);
        assertEquals(HttpStatus.OK,response.getStatusCode()); assertSame(inventory,response.getBody());
        verify(facade).findByProductId(10L);
    }
    @Test void save_shouldReturnCreated() {
        Inventory inventory=Inventory.builder().build(); when(facade.save(inventory)).thenReturn(inventory);
        var response=controller.save(inventory);
        assertEquals(HttpStatus.CREATED,response.getStatusCode()); assertSame(inventory,response.getBody());
        verify(facade).save(inventory);
    }
}
