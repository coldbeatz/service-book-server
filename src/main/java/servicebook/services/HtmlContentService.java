package servicebook.services;

import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import servicebook.resources.Resource;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import java.util.Base64;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HtmlContentService {

    private final FileUploadService fileUploadService;

    private final Map<String, String> extensionFromMimeType = Map.of(
        "image/jpeg", ".jpg",
        "image/png", ".png",
        "image/gif", ".gif",
        "image/webp", ".webp"
    );

    public String processContent(String htmlContent, User user) {
        htmlContent = htmlContent.replace("&nbsp;", " ");

        Document document = Jsoup.parse(htmlContent);
        Elements images = document.select("img[src^=data:image]");
        for (Element img : images) {
            String base64Data = img.attr("src");

            // Розбиваємо на data:image/jpeg;base64 і самі дані
            String[] parts = base64Data.split(",");
            if (parts.length != 2) {
                continue;
            }

            String mimeType = parts[0].substring(parts[0].indexOf(":") + 1, parts[0].indexOf(";"));
            String base64String = parts[1];

            String extension = extensionFromMimeType.get(mimeType);
            if (extension == null) {
                continue; // невідомий формат
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            if (imageBytes.length > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Image is too large");
            }

            String originalFilename = "image" + extension;

            Resource resource = fileUploadService.uploadFileToExternalHost(imageBytes, originalFilename, user);

            img.attr("src", resource.getUrl());
        }

        return document.body().html();
    }
}
