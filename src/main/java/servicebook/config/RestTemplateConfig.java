package servicebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestTemplate;

/**
 * Конфігурація для налаштування {@link RestTemplate}.
 *
 * <p>{@link RestTemplate} використовується для виконання HTTP-запитів у Spring-додатках.
 * Даний клас створює компонент {@link RestTemplate}, який можна використовувати для взаємодії
 * з зовнішніми REST API.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Створює та реєструє {@link RestTemplate} як Spring-компонент.
     *
     * <p>{@link RestTemplate} дозволяє надсилати HTTP-запити (GET, POST, PUT, DELETE тощо)
     * до зовнішніх веб-сервісів. Цей метод створює новий екземпляр {@link RestTemplate}
     * та додає його до контексту додатка.
     *
     * @return Новий екземпляр {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}