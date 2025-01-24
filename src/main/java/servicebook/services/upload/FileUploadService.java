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

import servicebook.repository.ResourceRepository;
import servicebook.resources.Resource;

import servicebook.user.User;

import java.io.IOException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${external.api.resource.server}")
    private String externalApiResourceServer;

    private final RestTemplate restTemplate;
    private final ResourceRepository resourceRepository;


    public Resource uploadFileToExternalHost(MultipartFile file, User user) throws IOException {
        return uploadFileToExternalHost(file, user, null);
    }

    public Resource uploadFileToExternalHost(MultipartFile file, User user, Resource resource) throws IOException {
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

        throw new FileUploadException("File upload failed");
    }

    public FileUploadResponse uploadFileToExternalHost(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<FileUploadResponse> response =
                restTemplate.postForEntity(externalApiResourceServer, requestEntity, FileUploadResponse.class);

        return response.getBody();
    }
}
