package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;

public interface ICategoryFacade {
    ResponseMessageUtilityDTO getCategories();
    ResponseMessageUtilityDTO getCategoriesForSeller(String email);
    ResponseMessageUtilityDTO getSubcategories(Long categoryId);
    ResponseMessageUtilityDTO getSubcategoriesForSeller(String email, Long categoryId);
    ResponseMessageUtilityDTO createCustomCategory(String email, CustomCategoryRequestDTO request);
    ResponseMessageUtilityDTO createCustomSubcategory(String email, Long categoryId, CustomSubcategoryRequestDTO request);
}
