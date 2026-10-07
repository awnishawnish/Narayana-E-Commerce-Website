package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Category;
import NarayanGroup.example.E_Commerce.model.Entity.Subcategory;

import java.util.List;

public interface ICategoryService {
    List<Category> getCategories();
    List<Category> getCategoriesForSeller(String email);
    List<Subcategory> getSubcategories(Long categoryId);
    List<Subcategory> getSubcategoriesForSeller(String email, Long categoryId);
    Category createCustomCategory(String email, CustomCategoryRequestDTO request);
    Subcategory createCustomSubcategory(String email, Long categoryId, CustomSubcategoryRequestDTO request);
    Category findCategoryById(Long categoryId);
    Subcategory findSubcategoryById(Long subcategoryId);
}
