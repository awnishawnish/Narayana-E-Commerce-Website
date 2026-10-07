package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVendorProductService {
    Product addProduct(Product product, Seller seller);
    List<Product> addProducts(List<Product> products, Seller seller);
    Page<Product> getProducts(Seller seller, Pageable pageable);
    Product updateProduct(Long productId, ProductUpdateRequestDTO request, Seller seller);
    void deleteProduct(Long productId, Seller seller);
    Product adjustQuantity(Long productId, int delta, Seller seller);
}
