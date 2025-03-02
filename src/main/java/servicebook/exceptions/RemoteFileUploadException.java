package servicebook.exceptions;

import servicebook.services.upload.FileUploadService;

/**
 * Помилка при завантаженні файлу на хостинг ресурсів за допомогою {@link FileUploadService}
 *
 * @see FileUploadService
 */
public class RemoteFileUploadException extends RuntimeException {

    public RemoteFileUploadException(String message) {
        super(message);
    }

    public RemoteFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
