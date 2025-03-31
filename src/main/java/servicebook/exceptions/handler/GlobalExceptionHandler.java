package servicebook.exceptions.handler;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

import servicebook.exceptions.*;

import servicebook.exceptions.handler.response.ErrorResponse;

/**
 * Глобальний обробник винятків для всієї програми
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обробляє виняток {@link UnauthorizedException} і повертає HTTP 401 (Unauthorized).
     * <p>
     * Може використовуватися, коли користувач неавторизований або токен недійсний
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        return buildError(e.getCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Обробляє виняток {@link EntityHasDependenciesException} і повертає HTTP 409 (Conflict).
     * <p>
     * Для випадків, коли сутність має пов'язані дані, і її не можна видалити
     */
    @ExceptionHandler(EntityHasDependenciesException.class)
    public ResponseEntity<ErrorResponse> handleEntityHasDependenciesException(EntityHasDependenciesException e) {
        return buildError("entity_has_dependencies", e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Обробляє виняток {@link BadRequestException} і повертає HTTP 400 (Bad Request).
     * <p>
     * Зазвичай використовується при помилках валідації вхідних параметрів
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return buildError(e.getCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обробляє виняток {@link AccessDeniedException} і повертає HTTP 403 (Forbidden).
     * <p>
     * Викликається, коли користувач не має прав на доступ до ресурсу
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return buildError("access_denied", e.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Обробляє виняток {@link RemoteFileUploadException} і повертає HTTP 502 (Bad Gateway).
     * <p>
     * Це може бути викликано помилкою при завантаженні файлу на зовнішній ресурс
     */
    @ExceptionHandler(RemoteFileUploadException.class)
    public ResponseEntity<ErrorResponse> handleRemoteFileUploadException(RemoteFileUploadException e) {
        return buildError("file_upload_failed", e.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    /**
     * Обробляє виняток {@link DuplicateEntityException} і повертає HTTP 409 (Conflict).
     * <p>
     * Використовується для запобігання дублюванню записів.
     * Наприклад як в {@link servicebook.services.CarBrandService#saveOrUpdate}
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntityException(DuplicateEntityException e) {
        return buildError("duplicate_entity", e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Обробляє виняток {@link EntityNotFoundException} і повертає HTTP 404 (Not Found).
     * <p>
     * Використовується при пошуку запису в БД
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildError("entity_not_found", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обробляє всі інші непередбачені винятки.
     * <p>
     * Використовується як fallback-обробник
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("Unexpected error occurred", e);

        return buildError("internal_error", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildError(String code, String message, HttpStatus status) {
        String safeMessage = message != null ? message : "No details provided";

        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, safeMessage, status));
    }
}
