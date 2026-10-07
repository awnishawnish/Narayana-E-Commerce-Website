package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ProductResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.facade.IVendorProductFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Category;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.model.Entity.Subcategory;
import NarayanGroup.example.E_Commerce.service.ICategoryService;
import NarayanGroup.example.E_Commerce.service.IS3Service;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import NarayanGroup.example.E_Commerce.service.IVendorProductService;
import NarayanGroup.example.E_Commerce.transformer.ProductTransformer;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class VendorProductFacade implements IVendorProductFacade {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(VendorProductFacade.class);

    private final IVendorProductService vendorProductService;
    private final ISellerService sellerService;
    private final ICategoryService categoryService;
    private final ProductTransformer productTransformer;
    private final IS3Service s3Service;

    @Override
    @Transactional
    public ResponseMessageUtilityDTO addProduct(String email, MultipartFile image, ProductRequestDTO request) throws IOException {
        Seller seller = sellerService.getActiveSellerByUserId(sellerService.getSellerByEmail(email).getUser().getId());
        Product product = productTransformer.toEntity(s3Service.uploadFile(image), request);
        applyCategory(product, request, seller);
        Product savedProduct = vendorProductService.addProduct(product, seller);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.SELLER_PRODUCT_ADDED_SUCCESSFULLY)
                .data(productTransformer.toResponseDTO(savedProduct))
                .build();
    }

    @Override
    @Transactional
    public ResponseMessageUtilityDTO addProducts(String email, List<ProductRequestDTO> requestList, List<MultipartFile> images) {
        Seller seller = sellerService.getActiveSellerByUserId(sellerService.getSellerByEmail(email).getUser().getId());
        Map<String, MultipartFile> imageMap = images.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, Function.identity()));

        List<Product> products = requestList.stream()
                .map(request -> {
                    MultipartFile image = imageMap.get(request.getImageName());
                    if (image == null) {
                        throw new CustomException.ImageNotFoundException(
                                ErrorConstants.IMAGE_NOT_FOUND_FOR_PRODUCT + request.getTitle()
                        );
                    }
                    try {
                        Product product = productTransformer.toEntity(s3Service.uploadFile(image), request);
                        applyCategory(product, request, seller);
                        return product;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<Product> savedProducts = vendorProductService.addProducts(products, seller);
        List<ProductResponseDTO> response = savedProducts.stream()
                .map(productTransformer::toResponseDTO)
                .toList();

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(String.format(CommonConstants.SELLER_PRODUCTS_ADDED_SUCCESSFULLY, savedProducts.size()))
                .data(response)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseMessageUtilityDTO getProducts(String email, Pageable pageable) {
        Seller seller = sellerService.getSellerByEmail(email);
        Page<ProductResponseDTO> response = vendorProductService.getProducts(seller, pageable)
                .map(productTransformer::toResponseDTO);

        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SELLER_PRODUCTS_FETCHED_SUCCESSFULLY)
                .data(response.getContent())
                .page(response.getNumber())
                .size(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .first(response.isFirst())
                .last(response.isLast())
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO updateProduct(String email, Long id, ProductUpdateRequestDTO request) {
        Seller seller = sellerService.getSellerByEmail(email);
        Product product = vendorProductService.updateProduct(id, request, seller);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SELLER_PRODUCT_UPDATED_SUCCESSFULLY)
                .data(productTransformer.toResponseDTO(product))
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO deleteProduct(String email, Long id) {
        Seller seller = sellerService.getSellerByEmail(email);
        vendorProductService.deleteProduct(id, seller);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SELLER_PRODUCT_DELETED_SUCCESSFULLY)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO adjustQuantity(String email, Long id, int delta) {
        Seller seller = sellerService.getSellerByEmail(email);
        Product product = vendorProductService.adjustQuantity(id, delta, seller);
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(String.format(CommonConstants.QUANTITY_UPDATED, product.getQuantity()))
                .data(productTransformer.toResponseDTO(product))
                .build();
    }

    private void applyCategory(Product product, ProductRequestDTO request, Seller seller) {
        if (request.getCategoryId() == null || request.getSubcategoryId() == null) {
            throw new IllegalArgumentException("Category and subcategory are required for vendor products");
        }

        Category category = categoryService.findCategoryById(request.getCategoryId());
        Subcategory subcategory = categoryService.findSubcategoryById(request.getSubcategoryId());

        if (!subcategory.getCategory().getId().equals(category.getId())) {
            throw new IllegalArgumentException(ErrorConstants.INVALID_CATEGORY_SUBCATEGORY);
        }

        if (category.isCustom()
                && category.getSeller() != null
                && !category.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException(ErrorConstants.CUSTOM_CATEGORY_ACCESS_DENIED);
        }

        if (subcategory.isCustom()
                && subcategory.getSeller() != null
                && !subcategory.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException(ErrorConstants.CUSTOM_SUBCATEGORY_ACCESS_DENIED);
        }

        product.setCategoryEntity(category);
        product.setSubcategory(subcategory);
        product.setCategory(category.getName());
    }
}
