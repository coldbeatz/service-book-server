package servicebook.services.upload;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import org.springframework.web.multipart.MultipartFile;

import servicebook.exceptions.RemoteFileUploadException;
import servicebook.repository.ResourceRepository;
import servicebook.resources.Resource;

import servicebook.user.User;

import java.io.IOException;

import java.time.LocalDateTime;

/**
 * Сервіс для завантаження файлів на зовнішній хост (файловий сервер)
 */
@Service
@RequiredArgsConstructor
public class FileUploadService {

    /**
     * URL зовнішнього сервера для завантаження файлів
     */
    @Value("${external.api.resource.server}")
    private String externalApiResourceServer;

    private final RestTemplate restTemplate;
    private final ResourceRepository resourceRepository;

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
     * Завантаження файлу на зовнішній хост та оновлення існуючого ресурсу або створення нового
     *
     * @param file Файл для завантаження
     * @param user Поточний користувач
     * @param resource Існуючий ресурс, який треба оновити (або null, якщо створюємо новий)
     *
     * @return Оновлений або створений ресурс
     */
    public Resource uploadFileToExternalHost(MultipartFile file, User user, Resource resource) {
        if (user == null)
            throw new NullPointerException("User is null");

        FileUploadResponse response = uploadFileToExternalHost(file);

        if (response.getStatus() == FileUploadStatus.SUCCESS) {
            if (resource != null) {
                resource.setUrl(response.getFileName());
                resource.setUser(user);
                resource.setUploadDate(LocalDateTime.now());
            } else {
                resource = Resource.createResource(response.getFileName(), user);
            }

            resourceRepository.save(resource);
            return resource;
        }

        throw new RemoteFileUploadException("File upload failed: " + response.getMessage());
    }

    /**
     * Завантаження файлу на зовнішній хост
     *
     * @param file Файл для завантаження
     * @return Відповідь від сервера
     */
    public FileUploadResponse uploadFileToExternalHost(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            throw new RemoteFileUploadException("Failed to read file content", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<FileUploadResponse> response =
                    restTemplate.postForEntity(externalApiResourceServer, requestEntity, FileUploadResponse.class);

            if (response.getBody() == null) {
                throw new RemoteFileUploadException("Empty response body from external resource server");
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RemoteFileUploadException("Failed to upload file to external resource server", e);
        }
    }
}
