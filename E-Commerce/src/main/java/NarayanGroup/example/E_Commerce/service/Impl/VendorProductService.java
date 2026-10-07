package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.model.Repositry.IProductRepository;
import NarayanGroup.example.E_Commerce.service.IVendorProductService;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import NarayanGroup.example.E_Commerce.transformer.InventoryTransformer;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class VendorProductService implements IVendorProductService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(VendorProductService.class);

    private final IProductRepository productRepository;
    private final IInventoryService inventoryService;
    private final InventoryTransformer inventoryTransformer;

    @Override
    @Transactional
    public Product addProduct(Product product, Seller seller) {
        product.setSeller(seller);
        Product savedProduct = productRepository.save(product);
        inventoryService.save(inventoryTransformer.toEntity(savedProduct, toRequestQuantity(product)));
        return savedProduct;
    }

    @Override
    @Transactional
    public List<Product> addProducts(List<Product> products, Seller seller) {
        products.forEach(product -> product.setSeller(seller));
        List<Product> savedProducts = productRepository.saveAll(products);
        savedProducts.forEach(product ->
                inventoryService.save(inventoryTransformer.toEntity(product, toRequestQuantity(product)))
        );
        return savedProducts;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Seller seller, Pageable pageable) {
        return productRepository.findBySellerIdAndIsDeletedFalse(seller.getId(), pageable);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductUpdateRequestDTO request, Seller seller) {
        Product product = productRepository
                .findByIdAndSellerIdAndIsDeletedFalse(productId, seller.getId())
                .orElseThrow(() -> new CustomException.ProductNotFoundException(
                        ErrorConstants.PRODUCT_NOT_FOUND_FOR_CURRENT_SELLER + productId
                ));

        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getName() != null) product.setName(request.getName());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Seller seller) {
        Product product = productRepository
                .findByIdAndSellerIdAndIsDeletedFalse(productId, seller.getId())
                .orElseThrow(() -> new CustomException.ProductNotFoundException(
                        ErrorConstants.PRODUCT_NOT_FOUND_FOR_CURRENT_SELLER + productId
                ));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public Product adjustQuantity(Long productId, int delta, Seller seller) {
        Product product = productRepository
                .findByIdAndSellerIdAndIsDeletedFalse(productId, seller.getId())
                .orElseThrow(() -> new CustomException.ProductNotFoundException(
                        ErrorConstants.PRODUCT_NOT_FOUND_FOR_CURRENT_SELLER + productId
                ));

        int currentQuantity = product.getQuantity() == null ? 0 : product.getQuantity();
        int newQuantity = currentQuantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException(ErrorConstants.QUANTITY_CANNOT_BE_NEGATIVE);
        }
        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }

    private NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO toRequestQuantity(Product product) {
        return NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO.builder()
                .quantity(product.getQuantity())
                .build();
    }
}
