package servicebook.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.crypto.password.PasswordEncoder;

import servicebook.user.UserService;

/**
 * Конфігурація для налаштування провайдера автентифікації Spring Security.
 * Використовується для автентифікації користувачів з використанням БД чи іншого джерела даних.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationProviderConfig {

    /**
     * Створює та налаштовує об'єкт {@link AuthenticationProvider}, який використовується для перевірки
     * автентичності користувачів.
     *
     * <p>Провайдер:
     * <ul>
     *   <li>Завантажує дані користувача за допомогою {@link UserService}.</li>
     *   <li>Перевіряє введений пароль за допомогою вказаного {@link PasswordEncoder}.</li>
     * </ul>
     *
     * @return Налаштований провайдер автентифікації {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}
