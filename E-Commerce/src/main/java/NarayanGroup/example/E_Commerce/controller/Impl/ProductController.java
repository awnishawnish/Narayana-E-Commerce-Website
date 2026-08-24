package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.ProductRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.ProductUpdateRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IProductController;
import NarayanGroup.example.E_Commerce.facade.IProductFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class ProductController implements IProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final IProductFacade iProductFacade;

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addProduct(
            MultipartFile image,
            String product
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDTO request = objectMapper.readValue(product, ProductRequestDTO.class);
        logger.info("Adding product: {}", request.getTitle());

        ResponseMessageUtilityDTO response = iProductFacade.addProduct(image, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> addProducts(
          String products,
            List<MultipartFile> images
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<ProductRequestDTO> requestList =
                objectMapper.readValue(
                        products,
                        objectMapper.getTypeFactory()
                                .constructCollectionType(
                                        List.class,
                                        ProductRequestDTO.class
                                )
                );

        ResponseMessageUtilityDTO response = iProductFacade.addProducts(requestList,images);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> updateProduct(
            Long id,
            ProductUpdateRequestDTO request
    ) {

        ResponseMessageUtilityDTO response = iProductFacade.updateProduct(id, request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> deleteProduct(Long id) {

        ResponseMessageUtilityDTO response = iProductFacade.deleteProduct(id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> adjustQuantity(
            Long id,
            int delta
    ) {

        ResponseMessageUtilityDTO response = iProductFacade.adjustQuantity(id, delta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getAllProducts(
            String category,
            Pageable pageable
    ) {

        ResponseMessageUtilityDTO response = iProductFacade.getAllProducts(category, pageable);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> searchProducts(
            String keyword,
            Pageable pageable
    ) {

        ResponseMessageUtilityDTO response = iProductFacade.searchProducts(keyword, pageable);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> searchByName(
            String name,
            Pageable pageable
    ) {

        ResponseMessageUtilityDTO response = iProductFacade.searchByName(name, pageable);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getProduct(Long id) {

        ResponseMessageUtilityDTO response = iProductFacade.findProduct(id);

        return ResponseEntity.ok(response);
    }
@Override
public String GetProduct(){
        return "Awnish";
    }
}
