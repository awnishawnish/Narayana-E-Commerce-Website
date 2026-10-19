package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.ICategoryFacade;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CategoryController implements NarayanGroup.example.E_Commerce.controller.ICategoryController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CategoryController.class);

    private final ICategoryFacade categoryFacade;
    public ResponseEntity<ResponseMessageUtilityDTO> getCategories() {
        logger.info("getCategories called");
        return ResponseEntity.ok(categoryFacade.getCategories());
    }
    public ResponseEntity<ResponseMessageUtilityDTO> getSubcategories(
            @PathVariable Long categoryId
    ) {
        logger.info("getSubcategories called for categoryId={}", categoryId);
        return ResponseEntity.ok(categoryFacade.getSubcategories(categoryId));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> createCustomCategory(
            @Valid @RequestBody CustomCategoryRequestDTO request,
            Authentication authentication
    ) {
        logger.info("createCustomCategory called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.status(201)
                .body(categoryFacade.createCustomCategory(authentication.getName(), request));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> createCustomSubcategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CustomSubcategoryRequestDTO request,
            Authentication authentication
    ) {
        logger.info("createCustomSubcategory called by user={} categoryId={}", authentication != null ? authentication.getName() : "anonymous", categoryId);
        return ResponseEntity.status(201)
                .body(categoryFacade.createCustomSubcategory(
                        authentication.getName(),
                        categoryId,
                        request
                ));
    }
}
