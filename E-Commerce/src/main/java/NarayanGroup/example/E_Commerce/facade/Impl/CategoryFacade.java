package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.CategoryResponseDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.DTO.response.SubcategoryResponseDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.facade.ICategoryFacade;
import NarayanGroup.example.E_Commerce.model.Entity.Category;
import NarayanGroup.example.E_Commerce.model.Entity.Subcategory;
import NarayanGroup.example.E_Commerce.service.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CategoryFacade implements ICategoryFacade {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CategoryFacade.class);

    private final ICategoryService categoryService;

    @Override
    public ResponseMessageUtilityDTO getCategories() {
        List<CategoryResponseDTO> response = categoryService.getCategories().stream()
                .map(this::toResponse)
                .toList();
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.CATEGORIES_FETCHED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO getCategoriesForSeller(String email) {
        List<CategoryResponseDTO> response = categoryService.getCategoriesForSeller(email).stream()
                .map(this::toResponse)
                .toList();
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.CATEGORIES_FETCHED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO getSubcategoriesForSeller(String email, Long categoryId) {
        List<SubcategoryResponseDTO> response = categoryService.getSubcategoriesForSeller(email, categoryId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SUBCATEGORIES_FETCHED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO getSubcategories(Long categoryId) {
        List<SubcategoryResponseDTO> response = categoryService.getSubcategories(categoryId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(200)
                .message(CommonConstants.SUBCATEGORIES_FETCHED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO createCustomCategory(String email, CustomCategoryRequestDTO request) {
        CategoryResponseDTO response = toResponse(categoryService.createCustomCategory(email, request));
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.CUSTOM_CATEGORY_CREATED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    @Override
    public ResponseMessageUtilityDTO createCustomSubcategory(String email, Long categoryId, CustomSubcategoryRequestDTO request) {
        SubcategoryResponseDTO response = toResponse(
                categoryService.createCustomSubcategory(email, categoryId, request)
        );
        return ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS)
                .httpStatus(201)
                .message(CommonConstants.CUSTOM_SUBCATEGORY_CREATED_SUCCESSFULLY)
                .data(response)
                .build();
    }

    private CategoryResponseDTO toResponse(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .custom(category.isCustom())
                .build();
    }

    private SubcategoryResponseDTO toResponse(Subcategory subcategory) {
        return SubcategoryResponseDTO.builder()
                .id(subcategory.getId())
                .categoryId(subcategory.getCategory().getId())
                .name(subcategory.getName())
                .description(subcategory.getDescription())
                .custom(subcategory.isCustom())
                .build();
    }
}
