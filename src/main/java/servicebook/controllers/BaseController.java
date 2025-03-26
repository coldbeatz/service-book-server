package servicebook.controllers;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import servicebook.user.User;

/**
 * Базовий контролер, повинен розширювати всі контролери, для зручності
 */
public class BaseController {

    /**
     * Отримує поточного аутентифікованого користувача із контексту безпеки.
     * <p>
     * Метод використовується для отримання {@link User} в усіх контролерах,
     * які успадковуються від {@link BaseController}.
     * <p>
     * У разі відсутності автентифікації або якщо користувач не авторизований,
     * буде викинуто {@link org.springframework.security.access.AccessDeniedException}.
     *
     * @return Аутентифікований користувач {@link User}
     * @throws org.springframework.security.access.AccessDeniedException якщо користувач не автентифікований
     */
    protected User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();
        }

        throw new AccessDeniedException("User is not authenticated");
    }
}
