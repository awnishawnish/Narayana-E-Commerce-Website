package NarayanGroup.example.E_Commerce.transformer;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductTransformer {
    private final IS3Service s3Service;

    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .category(product.getCategoryEntity() != null
                        ? product.getCategoryEntity().getName()
                        : product.getCategory())
                .categoryId(product.getCategoryEntity() != null
                        ? product.getCategoryEntity().getId()
                        : null)
                .subcategory(product.getSubcategory() != null
                        ? product.getSubcategory().getName()
                        : null)
                .subcategoryId(product.getSubcategory() != null
                        ? product.getSubcategory().getId()
                        : null)
                .sellerId(product.getSeller() != null
                        ? product.getSeller().getId()
                        : null)
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .currency(product.getCurrency())
                .imageKey(s3Service.getImageUrl(product.getImageKey()))
                .build();
    }

    public Product toEntity(String image, ProductRequestDTO request) {
        return Product.builder()
                .name(request.getName())
                .title(request.getTitle())
                .category(request.getCategory())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .currency(request.getCurrency())
                .imageKey(image)
                .isDeleted(false)
                .build();
    }
}
