package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.facade.IProductFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.service.IProductService;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.service.Impl.InventoryService;
import NarayanGroup.example.E_Commerce.transformer.InventoryTransformer;
import NarayanGroup.example.E_Commerce.transformer.ProductTransformer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductFacade implements IProductFacade {

    private static final Logger logger = LoggerFactory.getLogger(ProductFacade.class);
    private final IProductService iProductService;
    private final ProductTransformer productTransformer;
    private final IS3Service is3Service;
    private final InventoryService inventoryService;
    private final InventoryTransformer inventoryTransformer;

    @Override
    public ResponseMessageUtilityDTO addProduct(MultipartFile image, ProductRequestDTO request) throws IOException {
        logger.info("Adding product: {}", request.getTitle());
        String imageKey = is3Service.uploadFile(image);
        Product product = productTransformer.toEntity(imageKey, request);
        Product savedProduct = iProductService.addProduct(product);
        Inventory inventory = inventoryTransformer.toEntity(savedProduct, request);
        inventoryService.save(inventory);
        logger.debug("Product added: {}", product);
        ProductResponseDTO productResponse = productTransformer.toResponseDTO(savedProduct);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.PRODUCT_ADDED_SUCCESSFULLY)
                .data(productResponse)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO addProducts(List<ProductRequestDTO> requestList, List<MultipartFile> images) {

        Map<String, MultipartFile> imageMap = images.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, Function.identity()));
        List<Product> productList = requestList.stream()
                .map(request -> {
                    if (imageMap.containsKey(request.getImageName())) {
                        String imageKey = null;
                        try {
                            imageKey = is3Service.uploadFile(imageMap.get(request.getImageName()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return productTransformer.toEntity(imageKey, request);
                    } else {
                        throw new CustomException.ImageNotFoundException(ErrorConstants.IMAGE_NOT_FOUND_FOR_PRODUCT + request.getTitle());
                    }
                })
                .collect(Collectors.toList());
        List<Product> products = iProductService.addProducts(productList);
        List<ProductResponseDTO> productResponses = products.stream()
                .map(productTransformer::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Bulk products added: {}", products.size());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(String.format(CommonConstants.PRODUCTS_ADDED_SUCCESSFULLY, products.size()))
                .data(productResponses)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO getAllProducts(String category, Pageable pageable) {
        logger.info("Fetching products, category: {}", category);
        Page<Product> products = iProductService.getAllProducts(category, pageable);
        List<ProductResponseDTO> productResponses = products.getContent().stream()
                .map(productTransformer::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Fetched {} products", products.getTotalElements());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PRODUCTS_FETCHED_SUCCESSFULLY)
                .data(productResponses)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO searchProducts(String keyword, Pageable pageable) {
        logger.info("Searching products with keyword: {}", keyword);
        Page<Product> products = iProductService.searchProducts(keyword, pageable);
        List<ProductResponseDTO> productResponses = products.getContent().stream()
                .map(productTransformer::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Search returned {} products", products.getTotalElements());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(String.format(CommonConstants.SEARCH_RESULTS_FOR, keyword))
                .data(productResponses)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO findProduct(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = iProductService.findProduct(id);
        logger.debug("Fetched product: {}", product);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PRODUCT_FETCHED_SUCCESSFULLY)
                .data(productTransformer.toResponseDTO(product))
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO deleteProduct(Long id) {
        logger.warn("Deleting product with ID: {}", id);
        iProductService.deleteProduct(id);
        logger.info("Product deleted successfully, ID: {}", id);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PRODUCT_DELETED_SUCCESSFULLY)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO adjustQuantity(Long id, int delta) {
        logger.info("Adjusting quantity for product ID: {} with delta: {}", id, delta);
        Product product = iProductService.adjustQuantity(id, delta);
        logger.debug("Quantity updated for product ID: {}, new quantity: {}", id, product.getQuantity());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(String.format(CommonConstants.QUANTITY_UPDATED, product.getQuantity()))
                .data(productTransformer.toResponseDTO(product))
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO updateProduct(Long id, ProductUpdateRequestDTO request) {
        logger.info("Updating product with ID: {}", id);
        Product product = iProductService.updateProduct(id, request);
        logger.debug("Product updated: {}", product);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.PRODUCT_UPDATED_SUCCESSFULLY)
                .data(productTransformer.toResponseDTO(product))
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO searchByName(String name, Pageable pageable) {

        Page<Product> products = iProductService.searchByName(name, pageable);
        List<ProductResponseDTO> productResponses = products.stream()
                .map(productTransformer::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .message(CommonConstants.PRODUCTS_FETCHED_SUCCESSFULLY)
                .data(productResponses)
                .build();
    }
}
