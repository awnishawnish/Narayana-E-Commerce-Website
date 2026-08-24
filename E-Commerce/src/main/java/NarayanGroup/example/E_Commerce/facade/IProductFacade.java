package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// ✅ CHANGED: Facade now works with DTOs and returns ResponseMessageUtilityDTO
// (consistent with how UserFacade works — wraps everything in a standard response envelope)
public interface IProductFacade {

    ResponseMessageUtilityDTO addProduct(MultipartFile image,ProductRequestDTO request) throws IOException;

    ResponseMessageUtilityDTO addProducts(List<ProductRequestDTO> requestList,List<MultipartFile> images);

    // ✅ CHANGED: category is now optional (pass null for all products), + pageable for pagination
    ResponseMessageUtilityDTO getAllProducts(String category, Pageable pageable);

    // ✅ NEW: keyword search
    ResponseMessageUtilityDTO searchProducts(String keyword, Pageable pageable);

    ResponseMessageUtilityDTO findProduct(Long id);

    ResponseMessageUtilityDTO deleteProduct(Long id);

    // ✅ CHANGED: was addQuantity(Long id) — now takes explicit delta value
    ResponseMessageUtilityDTO adjustQuantity(Long id, int delta);

    // ✅ NEW: update product
    ResponseMessageUtilityDTO updateProduct(Long id, ProductUpdateRequestDTO request);

    ResponseMessageUtilityDTO searchByName(String name, Pageable pageable);
}
