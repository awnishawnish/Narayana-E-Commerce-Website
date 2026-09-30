package NarayanGroup.example.E_Commerce.service.Impl;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3ServiceTest {
    private final S3Client s3=mock(S3Client.class);

    private S3Service service() throws Exception {
        S3Service service=new S3Service(s3);
        Field bucket=S3Service.class.getDeclaredField("bucketName"); bucket.setAccessible(true); bucket.set(service,"test-bucket");
        Field region=S3Service.class.getDeclaredField("region"); region.setAccessible(true); region.set(service,"ap-south-1");
        return service;
    }

    @Test void getImageUrl_shouldBuildS3Url() throws Exception {
        assertEquals("https://test-bucket.s3.ap-south-1.amazonaws.com/key.jpg",service().getImageUrl("key.jpg"));
    }

    @Test void uploadFile_shouldSanitizeNameAndUpload() throws Exception {
        var file=new MockMultipartFile("image","my phone,1.jpg","image/jpeg","abc".getBytes());
        String key=service().uploadFile(file);
        assertTrue(key.matches("[0-9a-fA-F-]+_my_phone_1\\.jpg"));
        verify(s3).putObject(any(software.amazon.awssdk.services.s3.model.PutObjectRequest.class),any(software.amazon.awssdk.core.sync.RequestBody.class));
    }

    @Test void uploadFile_shouldUseFallbackNameForMissingOriginalName() throws Exception {
        var file=new MockMultipartFile("image",null,"image/jpeg","abc".getBytes());
        String key=service().uploadFile(file);
        assertTrue(key.endsWith("_file"));
    }
}
