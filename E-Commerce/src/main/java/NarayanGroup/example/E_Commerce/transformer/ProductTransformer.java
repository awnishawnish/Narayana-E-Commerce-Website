package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class ProductTransformer {
    private final IS3Service s3Service;

    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .category(product.getCategory())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .currency(product.getCurrency())
                .imageKey(s3Service.getImageUrl(product.getImageKey()))
                .build();
    }
    public Product toEntity(String  image, ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.getName())
                .title(request.getTitle())
                .category(request.getCategory())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .currency(request.getCurrency())
                .imageKey(image)
                .isDeleted(false)
                .build();
        return product;
    }
}
