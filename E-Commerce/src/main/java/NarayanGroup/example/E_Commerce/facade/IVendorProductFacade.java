package NarayanGroup.example.E_Commerce.facade;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IVendorProductFacade {
    ResponseMessageUtilityDTO addProduct(String email, MultipartFile image, ProductRequestDTO request) throws IOException;
    ResponseMessageUtilityDTO addProducts(String email, List<ProductRequestDTO> requestList, List<MultipartFile> images);
    ResponseMessageUtilityDTO getProducts(String email, Pageable pageable);
    ResponseMessageUtilityDTO updateProduct(String email, Long id, ProductUpdateRequestDTO request);
    ResponseMessageUtilityDTO deleteProduct(String email, Long id);
    ResponseMessageUtilityDTO adjustQuantity(String email, Long id, int delta);
}
