package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Repositry.IProductRepository;
import NarayanGroup.example.E_Commerce.service.IProductService;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final IProductRepository iProductRepository;
    private final IS3Service is3Service;


    @Override
    public Product addProduct(Product product) throws IOException {
        logger.info("Adding new product: {}", product.getTitle());
        return iProductRepository.save(product);

    }

    @Override
    public List<Product> addProducts(List<Product> requestList) {
        logger.info("Adding bulk products, count: {}", requestList.size());
        List<Product> products = requestList.stream()
                .map(request -> Product.builder()
                        .title(request.getTitle())
                        .category(request.getCategory())
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .currency(request.getCurrency())
                        .imageKey(request.getImageKey())
                        .isDeleted(false)
                        .build())
                .collect(Collectors.toList());
        return iProductRepository.saveAll(products);
    }

    @Override
    public Page<Product> getAllProducts(String category, Pageable pageable) {
        logger.info("Fetching products, category: {}", category);
        if (category != null && !category.isBlank()) {
            return iProductRepository
                    .findByCategoryAndIsDeletedFalse(category, pageable);
        }
        return iProductRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        logger.info("Searching products with keyword: {}", keyword);
        if (keyword == null || keyword.isBlank()) {
            logger.error("Search keyword cannot be empty");
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return iProductRepository.searchByTitleKeyword(keyword, pageable);
    }

    @Override
    public Product findProduct(Long id) {
        logger.info("Fetching product with ID: {}", id);
        return iProductRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new CustomException.UserNotFoundException("Product not found with id: " + id);
                });

    }

    @Override
    public void deleteProduct(Long id) {
        logger.warn("Deleting product with ID: {}", id);
        Product product = iProductRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new CustomException.UserNotFoundException("Product not found with id: " + id);
                });
        product.setDeleted(true);
        iProductRepository.save(product);
        logger.info("Product soft-deleted with ID: {}", id);
    }

    @Override
    public Product adjustQuantity(Long id, int delta) {
        logger.info("Adjusting quantity for product ID: {} with delta: {}", id, delta);
        Product product = iProductRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new CustomException.UserNotFoundException("Product not found with id: " + id);
                });

        int newQuantity = product.getQuantity() + delta;
        if (newQuantity < 0) {
            logger.error("Insufficient stock for product ID: {}. Available: {}", id, product.getQuantity());
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
        }
        product.setQuantity(newQuantity);
        return iProductRepository.save(product);

    }

    @Override
    public Product updateProduct(Long id, ProductUpdateRequestDTO request) {
        logger.info("Updating product with ID: {}", id);
        Product product = iProductRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new CustomException.UserNotFoundException("Product not found with id: " + id);
                });

        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getName() != null) product.setName(request.getName());
        return iProductRepository.save(product);

    }

    public Page<Product> getProductsByName(String name, Pageable pageable) {
        return iProductRepository.searchByName(name, pageable);
    }

    @Override
    public Page<Product> searchByName(String name, Pageable pageable) {
        logger.info("Searching products with keyword: {}", name);
        if (name == null || name.isBlank()) {
            logger.error("Search keyword cannot be empty");
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return iProductRepository.searchByName(name, pageable);
    }

   public Product getProductById(Long id){
        return iProductRepository.findById(id).orElseThrow(() -> {
            logger.error("Product not found with ID: {}", id);
            return new CustomException.UserNotFoundException("Product not found with id: " + id);
        });
    }

}
