package NarayanGroup.example.E_Commerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface  IS3Service {

    String uploadFile(MultipartFile file) throws IOException;

   String getImageUrl(String imageKey);


}
