package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/product")
public interface IProductController {

    @PostMapping("/add")
    ResponseEntity<ResponseMessageUtilityDTO> addProduct(
            @RequestPart("image") MultipartFile image,
          @RequestPart("product") String request
    ) throws IOException;

    @PostMapping("/add-bulk")
    ResponseEntity<ResponseMessageUtilityDTO> addProducts(
            @RequestPart("products") String products,
            @RequestPart("images") List<MultipartFile> images
    ) throws JsonProcessingException;

    @PutMapping("/update/{id}")
    ResponseEntity<ResponseMessageUtilityDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequestDTO request
    );

    @DeleteMapping("/delete/{id}")
    ResponseEntity<ResponseMessageUtilityDTO> deleteProduct(
            @PathVariable Long id
    );

    @PatchMapping("/{id}/quantity")
    ResponseEntity<ResponseMessageUtilityDTO> adjustQuantity(
            @PathVariable Long id,
            @RequestParam int delta
    );

    @GetMapping("/all")
    ResponseEntity<ResponseMessageUtilityDTO> getAllProducts(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable
    );

    @GetMapping("/search") 
    ResponseEntity<ResponseMessageUtilityDTO> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    );

    @GetMapping("/search-by-name")
    ResponseEntity<ResponseMessageUtilityDTO> searchByName(
            @RequestParam String name,
            @PageableDefault(size = 10) Pageable pageable
    );

    @GetMapping("/{id}")
    ResponseEntity<ResponseMessageUtilityDTO> getProduct(
            @PathVariable Long id
    );

    @GetMapping
    String GetProduct();
}
