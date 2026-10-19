package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.facade.IVendorProductFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class VendorProductController implements NarayanGroup.example.E_Commerce.controller.IVendorProductController {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VendorProductController.class);

    private final IVendorProductFacade vendorProductFacade;
    public ResponseEntity<ResponseMessageUtilityDTO> addProduct(
            @RequestPart("image") MultipartFile image,
            @RequestPart("product") String product,
            Authentication authentication
    ) throws IOException {
        ProductRequestDTO request = new ObjectMapper().readValue(product, ProductRequestDTO.class);
        logger.info("addProduct called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendorProductFacade.addProduct(authentication.getName(), image, request));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> addProducts(
            @RequestPart("products") String products,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProductRequestDTO> requestList = objectMapper.readValue(
                products,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProductRequestDTO.class)
        );
        logger.info("addProducts called by user={} count={}", authentication != null ? authentication.getName() : "anonymous", requestList != null ? requestList.size() : 0);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendorProductFacade.addProducts(authentication.getName(), requestList, images));
    }

    @GetMapping
    public ResponseEntity<ResponseMessageUtilityDTO> getProducts(
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication
    ) {
        logger.info("getProducts called by user={}", authentication != null ? authentication.getName() : "anonymous");
        return ResponseEntity.ok(vendorProductFacade.getProducts(authentication.getName(), pageable));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequestDTO request,
            Authentication authentication
    ) {
        logger.info("updateProduct called by user={} id={}", authentication != null ? authentication.getName() : "anonymous", id);
        return ResponseEntity.ok(vendorProductFacade.updateProduct(authentication.getName(), id, request));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> deleteProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        logger.info("deleteProduct called by user={} id={}", authentication != null ? authentication.getName() : "anonymous", id);
        return ResponseEntity.ok(vendorProductFacade.deleteProduct(authentication.getName(), id));
    }
    public ResponseEntity<ResponseMessageUtilityDTO> adjustQuantity(
            @PathVariable Long id,
            @RequestParam int delta,
            Authentication authentication
    ) {
        logger.info("adjustQuantity called by user={} id={} delta={}", authentication != null ? authentication.getName() : "anonymous", id, delta);
        return ResponseEntity.ok(vendorProductFacade.adjustQuantity(authentication.getName(), id, delta));
    }
    public String rename(){

        return "awnish sing ";
    }
}
