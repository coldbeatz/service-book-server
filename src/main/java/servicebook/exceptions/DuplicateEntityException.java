package servicebook.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Використовується для запобігання дублюванню записів.
 * <p>
 * Наприклад як в {@link servicebook.services.CarBrandService#saveOrUpdate}
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}