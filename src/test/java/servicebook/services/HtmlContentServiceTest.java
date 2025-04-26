package servicebook.services;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import servicebook.resources.Resource;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import java.util.Base64;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HtmlContentServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private HtmlContentService htmlContentService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();

        testUser.setId(1);
        testUser.setFullName("Test User");
    }

    @Test
    void shouldReplaceBase64ImagesWithUploadedUrls() {
        String base64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
        String htmlContent = "<p>Test Content</p><img src=\"" + base64Image + "\">";

        // Моканий ресурс, що повернеться з uploadFileToExternalHost
        Resource mockResource = Resource.createResource("https://fileserver.com/image.png", testUser);

        when(fileUploadService.uploadFileToExternalHost(any(byte[].class), anyString(), eq(testUser)))
                .thenReturn(mockResource);

        String processedHtml = htmlContentService.processContent(htmlContent, testUser);

        Document doc = Jsoup.parse(processedHtml);
        Element img = doc.selectFirst("img");

        assertNotNull(img);
        assertEquals("https://fileserver.com/image.png", img.attr("src"));
        verify(fileUploadService, times(1)).uploadFileToExternalHost(any(byte[].class), anyString(), eq(testUser));
    }

    @Test
    void shouldNotChangeContentWithoutImages() {
        String htmlContent = "<p>No images here</p>";
        String processedHtml = htmlContentService.processContent(htmlContent, testUser);

        assertEquals(processedHtml, htmlContent);
    }

    @Test
    void shouldThrowExceptionWhenImageIsTooLarge() {
        // Генеруємо base64 строку, яка при декодуванні дасть більше 5 МБ

        byte[] largeImage = new byte[6 * 1024 * 1024]; // 6 MB

        String base64Large = "data:image/png;base64," + Base64.getEncoder().encodeToString(largeImage);
        String htmlContent = "<p>Large image</p><img src=\"" + base64Large + "\">";

        assertThrows(IllegalArgumentException.class, () ->
                htmlContentService.processContent(htmlContent, testUser),
                "Image is too large"
        );
    }
}