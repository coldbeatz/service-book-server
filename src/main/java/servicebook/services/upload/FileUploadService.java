package servicebook.services.upload;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;

import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.reactive.function.client.WebClient;
import servicebook.exceptions.RemoteFileUploadException;

import servicebook.repository.ResourceRepository;

import servicebook.resources.Resource;

import servicebook.user.User;

import java.io.IOException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Сервіс для завантаження файлів на зовнішній хост (файловий сервер)
 */
@Service
public class FileUploadService {

    @Value("${external.resource.server.url}")
    private String resourceServerURL;

    private final WebClient webClient;

    private final ResourceRepository resourceRepository;

    public FileUploadService(ResourceRepository resourceRepository,
                             @Qualifier("externalApiWebClient") WebClient webClient) {

        this.resourceRepository = resourceRepository;
        this.webClient = webClient;
    }

    /**
     * Завантаження файлу на зовнішній хост зі створенням нового ресурсу в БД
     *
     * @param file Файл для завантаження
     * @param user Поточний користувач
     *
     * @return Збережений ресурс
     */
    public Resource uploadFileToExternalHost(MultipartFile file, User user) {
        return uploadFileToExternalHost(file, user, null);
    }

    /**
     * Завантаження файлу на зовнішній хост зі створенням нового ресурсу в БД
     *
     * @param fileBytes Байти файлу для завантаження
     * @param originalFilename Назва файла
     * @param user Поточний користувач
     *
     * @return Збережений ресурс
     */
    public Resource uploadFileToExternalHost(byte[] fileBytes, String originalFilename, User user) {
        return uploadFileToExternalHost(fileBytes, originalFilename, user, null);
    }

    /**
     * Завантаження файлу на зовнішній хост та оновлення існуючого ресурсу або створення нового
     *
     * @param file Файл для завантаження
     * @param user Поточний користувач
     * @param resource Існуючий ресурс, який треба оновити (або null, якщо створюємо новий)
     *
     * @return Оновлений або створений ресурс
     */
    public Resource uploadFileToExternalHost(MultipartFile file, User user, Resource resource) {
        try {
            return uploadFileToExternalHost(file.getBytes(), file.getOriginalFilename(), user, resource);
        } catch (IOException e) {
            throw new RemoteFileUploadException("Failed to read file content", e);
        }
    }

    /**
     * Завантаження файлу на зовнішній хост та оновлення існуючого ресурсу або створення нового
     *
     * @param fileBytes Байти файлу для завантаження
     * @param originalFilename Назва файла
     * @param user Поточний користувач
     * @param resource Існуючий ресурс, який треба оновити (або null, якщо створюємо новий)
     *
     * @return Оновлений або створений ресурс
     */
    public Resource uploadFileToExternalHost(byte[] fileBytes, String originalFilename, User user, Resource resource) {
        if (user == null)
            throw new IllegalArgumentException("User is null");

        FileUploadResponse response = sendFileToExternalServer(fileBytes, originalFilename);

        if (response.getStatus() == FileUploadStatus.SUCCESS) {
            String url = resourceServerURL + response.getFileName();

            if (resource != null) {
                resource.setUrl(url);
                resource.setUser(user);
                resource.setUploadDate(LocalDateTime.now());
            } else {
                resource = Resource.createResource(url, user);
            }

            resourceRepository.save(resource);
            return resource;
        }

        throw new RemoteFileUploadException("File upload failed: " + response.getMessage());
    }

    /**
     * Завантаження файлу на зовнішній хост
     *
     * @param fileBytes Байти файлу для завантаження
     * @param originalFilename Назва файла
     *
     * @return Відповідь від сервера
     */
    private FileUploadResponse sendFileToExternalServer(byte[] fileBytes, String originalFilename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return originalFilename;
            }
        });

        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(msg -> new RemoteFileUploadException("External server error: " + msg))
                )
                .bodyToMono(FileUploadResponse.class)
                .timeout(Duration.ofSeconds(15))
                .block();
    }

    /**
     * Завантаження файлу на зовнішній хост
     *
     * @param file Файл для завантаження
     *
     * @return Відповідь від сервера
     */
    @SuppressWarnings("unused")
    private FileUploadResponse sendFileToExternalServer(MultipartFile file) {
        try {
            return sendFileToExternalServer(file.getBytes(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new RemoteFileUploadException("Failed to read file content", e);
        }
    }
}
