package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IProductFacade;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    private final IProductFacade facade=mock(IProductFacade.class);
    private final ProductController controller=new ProductController(facade);

    @Test void addProduct_shouldParseJsonAndDelegate() throws Exception {
        var image=new MockMultipartFile("image","phone.jpg","image/jpeg",new byte[]{1});
        var body=ResponseMessageUtilityDTO.builder().build();
        when(facade.addProduct(eq(image),any())).thenReturn(body);
        var response=controller.addProduct(image,"{\"title\":\"Phone\",\"quantity\":2}");
        assertEquals(201,response.getStatusCode().value()); assertSame(body,response.getBody());
        verify(facade).addProduct(eq(image),argThat(p -> "Phone".equals(p.getTitle()) && p.getQuantity()==2));
    }
    @Test void addProducts_shouldParseListAndDelegate() throws Exception {
        var image=new MockMultipartFile("images","phone.jpg","image/jpeg",new byte[]{1});
        var body=ResponseMessageUtilityDTO.builder().build();
        when(facade.addProducts(anyList(),eq(List.of(image)))).thenReturn(body);
        var response=controller.addProducts("[{\"title\":\"Phone\",\"quantity\":2}]",List.of(image));
        assertEquals(201,response.getStatusCode().value()); assertSame(body,response.getBody());
        verify(facade).addProducts(argThat(l -> l.size()==1 && "Phone".equals(l.get(0).getTitle())),eq(List.of(image)));
    }
    @Test void updateProduct_shouldReturnOk() {
        var req=new ProductUpdateRequestDTO(); var body=ResponseMessageUtilityDTO.builder().build();
        when(facade.updateProduct(1L,req)).thenReturn(body);
        var response=controller.updateProduct(1L,req);
        assertEquals(200,response.getStatusCode().value()); assertSame(body,response.getBody());
    }
    @Test void deleteProduct_shouldReturnOk() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.deleteProduct(1L)).thenReturn(body);
        assertSame(body,controller.deleteProduct(1L).getBody());
    }
    @Test void adjustQuantity_shouldReturnOk() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.adjustQuantity(1L,3)).thenReturn(body);
        assertSame(body,controller.adjustQuantity(1L,3).getBody());
    }
    @Test void getAllProducts_shouldDelegate() {
        var page=PageRequest.of(0,10); var body=ResponseMessageUtilityDTO.builder().build(); when(facade.getAllProducts("Mobile",page)).thenReturn(body);
        assertSame(body,controller.getAllProducts("Mobile",page).getBody());
    }
    @Test void searchProducts_shouldDelegate() {
        var page=PageRequest.of(0,10); var body=ResponseMessageUtilityDTO.builder().build(); when(facade.searchProducts("phone",page)).thenReturn(body);
        assertSame(body,controller.searchProducts("phone",page).getBody());
    }
    @Test void searchByName_shouldDelegate() {
        var page=PageRequest.of(0,10); var body=ResponseMessageUtilityDTO.builder().build(); when(facade.searchByName("phone",page)).thenReturn(body);
        assertSame(body,controller.searchByName("phone",page).getBody());
    }
    @Test void getProduct_shouldDelegate() {
        var body=ResponseMessageUtilityDTO.builder().build(); when(facade.findProduct(1L)).thenReturn(body);
        assertSame(body,controller.getProduct(1L).getBody());
    }
    @Test void GetProduct_shouldReturnLegacyValue() {
        assertEquals("Awnish",controller.GetProduct());
    }
}
