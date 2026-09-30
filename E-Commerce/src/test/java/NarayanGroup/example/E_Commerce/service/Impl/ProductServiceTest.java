package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Repositry.IProductRepository;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private final IProductRepository repository=mock(IProductRepository.class);
    private final IS3Service s3=mock(IS3Service.class);
    private final ProductService service=new ProductService(repository,s3);
    private Product p(){return Product.builder().id(1L).title("Phone").category("Mobile").price(new BigDecimal("100")).quantity(5).currency("INR").isDeleted(false).build();}
    @Test void addProduct_shouldSave() throws Exception { var p=p(); when(repository.save(p)).thenReturn(p); assertSame(p,service.addProduct(p)); }
    @Test void addProducts_shouldMapAndSave() { var p=p(); when(repository.saveAll(anyList())).thenAnswer(i->i.getArgument(0)); assertEquals(1,service.addProducts(List.of(p)).size()); }
    @Test void getAllProducts_shouldUseCategoryOrAll() {
        Pageable pageable=PageRequest.of(0,10); Page<Product> page=new PageImpl<>(List.of(p()));
        when(repository.findByCategoryAndIsDeletedFalse("Mobile",pageable)).thenReturn(page);
        when(repository.findByIsDeletedFalse(pageable)).thenReturn(page);
        assertSame(page,service.getAllProducts("Mobile",pageable)); assertSame(page,service.getAllProducts("",pageable));
        assertSame(page,service.getAllProducts(null,pageable));
    }
    @Test void searchProducts_shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class,()->service.searchProducts("",PageRequest.of(0,10)));
        var page=new PageImpl<Product>(List.of()); when(repository.searchByTitleKeyword("phone",PageRequest.of(0,10))).thenReturn(page);
        assertSame(page,service.searchProducts("phone",PageRequest.of(0,10)));
    }
    @Test void findDeleteAdjust_shouldCoverBranches() {
        var p=p(); when(repository.findById(1L)).thenReturn(Optional.of(p)); assertSame(p,service.findProduct(1L));
        p.setDeleted(true); assertThrows(CustomException.UserNotFoundException.class,()->service.findProduct(1L)); p.setDeleted(false);
        service.deleteProduct(1L); assertTrue(p.isDeleted()); verify(repository).save(p);
        p.setQuantity(5); when(repository.findById(1L)).thenReturn(Optional.of(p)); when(repository.save(p)).thenReturn(p);
        assertEquals(7,service.adjustQuantity(1L,2).getQuantity()); assertThrows(IllegalArgumentException.class,()->service.adjustQuantity(1L,-10));
        when(repository.findById(99L)).thenReturn(Optional.empty()); assertThrows(RuntimeException.class,()->service.deleteProduct(99L));
    }
    @Test void updateAndSearchByName_shouldWork() {
        var p=p(); when(repository.findById(1L)).thenReturn(Optional.of(p)); when(repository.save(p)).thenReturn(p);
        var req=new ProductUpdateRequestDTO(); req.setTitle("New"); req.setQuantity(9); req.setPrice(new BigDecimal("120")); req.setCurrency("USD"); req.setCategory("Tech"); req.setName("N");
        assertSame(p,service.updateProduct(1L,req)); assertEquals("New",p.getTitle()); assertEquals(9,p.getQuantity());
        var page=new PageImpl<Product>(List.of(p)); Pageable pageable=PageRequest.of(0,10); when(repository.searchByName("phone",pageable)).thenReturn(page);
        assertSame(page,service.getProductsByName("phone",pageable)); assertSame(page,service.searchByName("phone",pageable));
        assertThrows(IllegalArgumentException.class,()->service.searchByName(" ",pageable));
    }
    @Test void getProductById_shouldReturnActiveOrThrow() {
        var p=p(); when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(p)); assertSame(p,service.getProductById(1L));
        when(repository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.empty()); assertThrows(CustomException.ProductNotFoundException.class,()->service.getProductById(2L));
    }
}
