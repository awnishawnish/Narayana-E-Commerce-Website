package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.service.IProductService;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import NarayanGroup.example.E_Commerce.service.Impl.InventoryService;
import NarayanGroup.example.E_Commerce.transformer.InventoryTransformer;
import NarayanGroup.example.E_Commerce.transformer.ProductTransformer;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductFacadeTest {
    private final IProductService productService=mock(IProductService.class);
    private final ProductTransformer productTransformer=mock(ProductTransformer.class);
    private final IS3Service s3Service=mock(IS3Service.class);
    private final InventoryService inventoryService=mock(InventoryService.class);
    private final InventoryTransformer inventoryTransformer=mock(InventoryTransformer.class);
    private final ProductFacade facade=new ProductFacade(productService,productTransformer,s3Service,inventoryService,inventoryTransformer);

    private ProductRequestDTO request() {
        return ProductRequestDTO.builder().title("Phone").name("P").category("Mobile").price(new BigDecimal("100")).quantity(5).currency("INR").imageName("phone.jpg").build();
    }
    private Product product() { return Product.builder().id(1L).title("Phone").price(new BigDecimal("100")).quantity(5).currency("INR").build(); }
    private ProductResponseDTO response() { return ProductResponseDTO.builder().id(1L).title("Phone").build(); }

    @Test void addProduct_shouldUploadSaveInventoryAndReturnResponse() throws IOException {
        var image=new MockMultipartFile("image","phone.jpg","image/jpeg",new byte[]{1}); var req=request(); var saved=product(); var dto=response();
        when(s3Service.uploadFile(image)).thenReturn("key"); when(productTransformer.toEntity("key",req)).thenReturn(saved);
        when(productService.addProduct(saved)).thenReturn(saved); when(inventoryTransformer.toEntity(saved,req)).thenReturn(Inventory.builder().product(saved).build());
        when(productTransformer.toResponseDTO(saved)).thenReturn(dto);
        var r=facade.addProduct(image,req);
        assertEquals(201,r.getHttpStatus()); assertSame(dto,r.getData());
        verify(inventoryService).save(any(Inventory.class));
    }

    @Test void addProducts_shouldMapImagesAndReturnAllProducts() throws Exception {
        var image=new MockMultipartFile("image","phone.jpg","image/jpeg",new byte[]{1}); var req=request(); var p=product(); var dto=response();
        when(s3Service.uploadFile(image)).thenReturn("key"); when(productTransformer.toEntity("key",req)).thenReturn(p);
        when(productService.addProducts(anyList())).thenReturn(List.of(p)); when(productTransformer.toResponseDTO(p)).thenReturn(dto);
        var r=facade.addProducts(List.of(req),List.of(image));
        assertEquals(201,r.getHttpStatus()); assertEquals(1,((List<?>)r.getData()).size());
    }

    @Test void addProducts_shouldRejectMissingImage() {
        var req=request();
        assertThrows(CustomException.ImageNotFoundException.class,()->facade.addProducts(List.of(req),List.of()));
        verifyNoInteractions(productService);
    }

    @Test void getAllProducts_shouldMapPage() {
        var page=PageRequest.of(0,10); var p=product(); var dto=response();
        when(productService.getAllProducts("Mobile",page)).thenReturn(new PageImpl<>(List.of(p)));
        when(productTransformer.toResponseDTO(p)).thenReturn(dto);
        var r=facade.getAllProducts("Mobile",page);
        assertEquals(200,r.getHttpStatus()); assertEquals(1,((List<?>)r.getData()).size());
    }

    @Test void searchProducts_shouldUseKeywordInMessage() {
        var page=PageRequest.of(0,10); var p=product(); when(productService.searchProducts("phone",page)).thenReturn(new PageImpl<>(List.of(p)));
        when(productTransformer.toResponseDTO(p)).thenReturn(response());
        var r=facade.searchProducts("phone",page);
        assertTrue(r.getMessage().contains("phone"));
    }

    @Test void findAndDeleteProduct_shouldDelegate() {
        var p=product(); when(productService.findProduct(1L)).thenReturn(p); when(productTransformer.toResponseDTO(p)).thenReturn(response());
        assertEquals(200,facade.findProduct(1L).getHttpStatus());
        assertEquals(200,facade.deleteProduct(1L).getHttpStatus()); verify(productService).deleteProduct(1L);
    }

    @Test void adjustQuantityAndUpdate_shouldReturnTransformedProduct() {
        var p=product(); when(productService.adjustQuantity(1L,2)).thenReturn(p); when(productService.updateProduct(eq(1L),any(ProductUpdateRequestDTO.class))).thenReturn(p);
        when(productTransformer.toResponseDTO(p)).thenReturn(response());
        assertEquals(200,facade.adjustQuantity(1L,2).getHttpStatus());
        assertEquals(200,facade.updateProduct(1L,new ProductUpdateRequestDTO()).getHttpStatus());
    }

    @Test void searchByName_shouldReturnProducts() {
        var page=PageRequest.of(0,10); var p=product(); when(productService.searchByName("phone",page)).thenReturn(new PageImpl<>(List.of(p)));
        when(productTransformer.toResponseDTO(p)).thenReturn(response());
        var r=facade.searchByName("phone",page);
        assertEquals(1,((List<?>)r.getData()).size());
    }

    @Test void addProducts_shouldWrapImageUploadFailure() throws Exception {
        var image=new MockMultipartFile("image","phone.jpg","image/jpeg",new byte[]{1});
        when(s3Service.uploadFile(image)).thenThrow(new IOException("s3 unavailable"));
        assertThrows(RuntimeException.class,()->facade.addProducts(List.of(request()),List.of(image)));
        verifyNoInteractions(productService);
    }

}
