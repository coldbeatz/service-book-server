package servicebook.exceptions.handler;

import jakarta.persistence.EntityNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

import servicebook.exceptions.*;

import servicebook.utils.responce.ErrorResponse;

import java.util.Map;

/**
 * Глобальний обробник винятків для всієї програми
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ENTITY_NOT_FOUND_CODE = "entity_not_found";
    private static final String FILE_UPLOAD_FAILED_CODE = "file_upload_failed";

    @Getter
    @Builder
    @AllArgsConstructor
    private static class HandlerErrorResponse {

        private String error;
        private String message;
    }

    /**
     * Обробляє виняток {@link EntityHasDependenciesException} і повертає HTTP 409 (Conflict)
     */
    @ExceptionHandler(EntityHasDependenciesException.class)
    public ResponseEntity<?> handleEntityHasDependenciesException(EntityHasDependenciesException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new HandlerErrorResponse("entity_has_dependencies", e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "error", "Forbidden",
                        "message", ex.getMessage(),
                        "timestamp", System.currentTimeMillis()
                ));
    }

    /**
     * Обробляє виняток {@link RemoteFileUploadException}.
     * Це може бути викликано помилкою при завантаженні файлу на зовнішній ресурс
     */
    @ExceptionHandler(RemoteFileUploadException.class)
    public ResponseEntity<String> handleRemoteFileUploadException(RemoteFileUploadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(FILE_UPLOAD_FAILED_CODE);
    }

    /**
     * Обробляє виняток {@link DuplicateEntityException} і повертає HTTP 409 (Conflict)
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<String> handleDuplicateEntityException(DuplicateEntityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Обробляє виняток {@link EntityNotFoundException} і повертає HTTP 404 (Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ENTITY_NOT_FOUND_CODE);
    }

    /**
     * Обробляє виняток {@link ClientException}
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getCode()));
    }
}
