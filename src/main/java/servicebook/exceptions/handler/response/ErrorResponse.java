package servicebook.exceptions.handler.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO-клас для представлення структури відповіді з помилкою.
 * <p>
 * Використовується в глобальному обробнику винятків (@ControllerAdvice)
 * для повернення уніфікованих JSON-обʼєктів з інформацією про помилки.
 *
 * @param error     Унікальний код помилки (наприклад, "invalid_token")
 * @param message   Людиночитабельне повідомлення про помилку
 * @param status    HTTP-статус у числовому вигляді (наприклад, 400, 401, 500)
 * @param timestamp Дата та час, коли сталася помилка
 */
public record ErrorResponse(String error, String message, int status, LocalDateTime timestamp) {

    /**
     * Конструктор для зручного створення ErrorResponse з HttpStatus.
     *
     * @param error   Унікальний код помилки
     * @param message Людиночитабельне повідомлення
     * @param status  HTTP-статус ({@link HttpStatus})
     *
     * @see HttpStatus
     */
    public ErrorResponse(String error, String message, HttpStatus status) {
        this(error, message, status.value(), LocalDateTime.now());
    }

    /**
     * Конструктор для явного задання числового статусу (без HttpStatus enum).
     *
     * @param error   Унікальний код помилки
     * @param message Людиночитабельне повідомлення
     * @param status  HTTP-статус (наприклад, 400)
     */
    public ErrorResponse(String error, String message, int status) {
        this(error, message, status, LocalDateTime.now());
    }
}