package NarayanGroup.example.E_Commerce.controller;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vendor/products")
public interface IVendorProductController {
    @PostMapping("/add") ResponseEntity<ResponseMessageUtilityDTO> addProduct(@RequestPart("image") MultipartFile image, @RequestPart("product") String product, Authentication authentication) throws IOException;
    @PostMapping("/add-bulk") ResponseEntity<ResponseMessageUtilityDTO> addProducts(@RequestPart("products") String products, @RequestPart("images") List<MultipartFile> images, Authentication authentication) throws JsonProcessingException;
    @GetMapping ResponseEntity<ResponseMessageUtilityDTO> getProducts(@PageableDefault(size = 10) Pageable pageable, Authentication authentication);
    @PutMapping("/{id}") ResponseEntity<ResponseMessageUtilityDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequestDTO request, Authentication authentication);
    @DeleteMapping("/{id}") ResponseEntity<ResponseMessageUtilityDTO> deleteProduct(@PathVariable Long id, Authentication authentication);
    @PatchMapping("/{id}/quantity") ResponseEntity<ResponseMessageUtilityDTO> adjustQuantity(@PathVariable Long id, @RequestParam int delta, Authentication authentication);
    @GetMapping("/rename") String rename();
}
