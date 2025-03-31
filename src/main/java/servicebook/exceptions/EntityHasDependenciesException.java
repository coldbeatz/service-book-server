package servicebook.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Виняток для випадків, коли сутність має пов'язані дані, і її не можна видалити
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class EntityHasDependenciesException extends RuntimeException {

    public EntityHasDependenciesException(String message) {
        super(message);
    }

    public EntityHasDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}
