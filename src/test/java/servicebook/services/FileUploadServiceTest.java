package servicebook.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import org.springframework.web.multipart.MultipartFile;
import servicebook.repository.ResourceRepository;
import servicebook.resources.Resource;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;
import servicebook.user.UserRepository;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findById(13L).orElseThrow();
    }

    @Test
    void testUploadFileToExternalHost_WithMultipartFile() throws IOException {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                Files.readAllBytes(new ClassPathResource("test-image.png").getFile().toPath())
        );

        Resource resource = fileUploadService.uploadFileToExternalHost(mockFile, testUser);
        assertNotNull(resource);
    }

    @Test
    void testUploadFileToExternalHost_WithMultipartFileAndExistingResource() throws IOException {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                Files.readAllBytes(new ClassPathResource("test-image.png").getFile().toPath())
        );

        // Спочатку створимо новий ресурс
        Resource existingResource = fileUploadService.uploadFileToExternalHost(mockFile, testUser);
        LocalDateTime previousDate = existingResource.getUploadDate();

        // Тепер оновимо цей ресурс
        Resource updatedResource = fileUploadService.uploadFileToExternalHost(mockFile, testUser, existingResource);

        assertNotNull(updatedResource);
        assertEquals(existingResource.getId(), updatedResource.getId());
        assertNotEquals(previousDate, updatedResource.getUploadDate()); // Дата повинна оновитись
    }

    @Test
    void shouldUploadRealFileToExternalServer() throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of("src/test/resources/test-image.png"));
        String originalFilename = "test-image.png";

        Resource resource = fileUploadService.uploadFileToExternalHost(fileBytes, originalFilename, testUser);

        assertNotNull(resource);
    }

    @Test
    void testUploadFileToExternalHost_WithBytesFilenameAndExistingResource() throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of("src/test/resources/test-image.png"));;
        String originalFilename = "test-image.png";

        Resource existingResource = resourceRepository.findById(128L).orElseThrow();
        LocalDateTime previousDate = existingResource.getUploadDate();

        // Потім оновимо цей же ресурс
        Resource updatedResource = fileUploadService.uploadFileToExternalHost(fileBytes, originalFilename,
                testUser, existingResource);

        assertNotNull(updatedResource);
        assertEquals(existingResource.getId(), updatedResource.getId());
        assertNotEquals(previousDate, updatedResource.getUploadDate());
    }
}