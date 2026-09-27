package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductTransformerTest {
    @Test void toResponseDTO_shouldMapProductAndImage() {
        IS3Service s3 = mock(IS3Service.class);
        when(s3.getImageUrl("key")).thenReturn("url");
        Product product = Product.builder().id(1L).title("Phone").name("P").category("C")
                .price(new BigDecimal("100.00")).quantity(4).currency("INR").imageKey("key").build();
        var result = new ProductTransformer(s3).toResponseDTO(product);
        assertEquals(1L, result.getId());
        assertEquals("url", result.getImageKey());
        assertEquals(4, result.getQuantity());
    }

    @Test void toEntity_shouldMapRequest() {
        IS3Service s3 = mock(IS3Service.class);
        ProductRequestDTO request = ProductRequestDTO.builder().name("P").title("Phone").category("C")
                .price(new BigDecimal("100.00")).quantity(4).currency("INR").build();
        Product result = new ProductTransformer(s3).toEntity("key", request);
        assertEquals("Phone", result.getTitle());
        assertEquals(4, result.getQuantity());
        assertFalse(result.isDeleted());
    }
}
