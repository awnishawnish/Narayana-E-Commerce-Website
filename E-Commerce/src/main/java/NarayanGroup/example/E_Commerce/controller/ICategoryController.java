package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public interface ICategoryController {
    @GetMapping("/all") ResponseEntity<ResponseMessageUtilityDTO> getCategories();
    @GetMapping("/{categoryId}/subcategories") ResponseEntity<ResponseMessageUtilityDTO> getSubcategories(@PathVariable Long categoryId);
    @PostMapping("/custom") ResponseEntity<ResponseMessageUtilityDTO> createCustomCategory(@Valid @RequestBody CustomCategoryRequestDTO request, Authentication authentication);
    @PostMapping("/{categoryId}/custom-subcategory") ResponseEntity<ResponseMessageUtilityDTO> createCustomSubcategory(@PathVariable Long categoryId, @Valid @RequestBody CustomSubcategoryRequestDTO request, Authentication authentication);
}
