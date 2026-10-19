package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor/categories")
public interface IVendorCategoryController {
    @GetMapping ResponseEntity<ResponseMessageUtilityDTO> getCategories(Authentication authentication);
    @GetMapping("/{categoryId}/subcategories") ResponseEntity<ResponseMessageUtilityDTO> getSubcategories(@PathVariable Long categoryId, Authentication authentication);
}
