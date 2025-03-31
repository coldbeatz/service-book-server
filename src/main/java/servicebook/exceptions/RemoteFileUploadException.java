package servicebook.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

import servicebook.services.upload.FileUploadService;

/**
 * Помилка при завантаженні файлу на хостинг ресурсів за допомогою {@link FileUploadService}
 *
 * @see FileUploadService
 */
@ResponseStatus(code = HttpStatus.BAD_GATEWAY)
public class RemoteFileUploadException extends RuntimeException {

    public RemoteFileUploadException(String message) {
        super(message);
    }

    public RemoteFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
