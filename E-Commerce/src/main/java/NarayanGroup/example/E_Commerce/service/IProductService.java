package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProductService {

    // ✅ CHANGED: now accepts ProductRequestDTO instead of Product entity
    Product addProduct(Product product) throws IOException;

    // ✅ CHANGED: now accepts list of DTOs, returns Page
    // (bulk add returns list, no pagination needed here)
    java.util.List<Product> addProducts(java.util.List<Product> requestList);

    // ✅ CHANGED: returns Page<ProductResponseDTO> with optional category filter
    // category = null → returns all products; category provided → filters by category
    Page<Product> getAllProducts(String category, Pageable pageable);

    // ✅ NEW: search products by keyword in title
    Page<Product> searchProducts(String keyword, Pageable pageable);

    // ✅ CHANGED: returns ProductResponseDTO instead of Optional<Product>
    Product findProduct(Long id);

    // (no change in signature)
    void deleteProduct(Long id);

    // ✅ CHANGED: was `addQuantity(Long id)` — now accepts explicit delta (positive = add, negative = reduce)
    // Example: adjustQuantity(1L, +5) adds 5 units; adjustQuantity(1L, -2) removes 2 units
    Product adjustQuantity(Long id, int delta);

    // ✅ NEW: update product fields (partial update — only non-null fields applied)
    Product updateProduct(Long id, ProductUpdateRequestDTO request);
    public Page<Product>getProductsByName(String name, Pageable pageable);

    Page<Product> searchByName(String name, Pageable pageable);

    Product getProductById(Long id);
}
