package servicebook.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettingsResponse {

    /**
     * Чи було надіслано лист на пошту для підтвердження нового e-mail
     */
    private boolean emailConfirmationSent;

    /**
     * Чи були змінені дані користувача (пароль, ім'я, або інші дані)
     */
    private boolean userUpdated;

    /**
     * Токен авторизації (якщо пароль користувача був оновлений)
     */
    private String token;

    /**
     * Пароль було змінено або встановлено
     */
    private boolean updatedPassword;
}
