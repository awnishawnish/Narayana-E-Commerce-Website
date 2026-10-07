package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.ICategoryFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor/categories")
@AllArgsConstructor
public class VendorCategoryController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(VendorCategoryController.class);

    private final ICategoryFacade categoryFacade;

    @GetMapping
    public ResponseEntity<ResponseMessageUtilityDTO> getCategories(Authentication authentication) {
        logger.info("getCategories (vendor) called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.ok(categoryFacade.getCategoriesForSeller(authentication.getName()));
    }

    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<ResponseMessageUtilityDTO> getSubcategories(
            @PathVariable Long categoryId,
            Authentication authentication
    ) {
        logger.info("getSubcategories (vendor) called by user={} categoryId={}", authentication != null ? authentication.getName() : "anonymous", categoryId);
        return ResponseEntity.ok(
                categoryFacade.getSubcategoriesForSeller(authentication.getName(), categoryId)
        );
    }
}
