package servicebook.exceptions;

/**
 * Виняток для випадків, коли сутність має пов'язані дані, і її не можна видалити
 */
public class EntityHasDependenciesException extends RuntimeException {

    public EntityHasDependenciesException(String message) {
        super(message);
    }

    public EntityHasDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}
