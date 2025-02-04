package servicebook.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import servicebook.exceptions.ClientException;
import servicebook.exceptions.DuplicateEntityException;

import servicebook.utils.responce.ErrorResponse;

/**
 * Глобальний обробник винятків для всієї програми
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обробляє виняток {@link DuplicateEntityException} і повертає HTTP 409 (Conflict).
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<String> handleDuplicateEntityException(DuplicateEntityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Обробляє виняток {@link ClientException}
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getCode()));
    }
}
