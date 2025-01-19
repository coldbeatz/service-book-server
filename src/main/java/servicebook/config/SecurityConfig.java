package servicebook.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

import servicebook.services.jwt.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Клас конфігурації безпеки для Spring Security.
 * <p>
 * Цей клас визначає налаштування безпеки для застосунку, включаючи правила доступу,
 * CORS-конфігурацію та інтеграцію з JWT-фільтром.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Налаштовує ланцюг безпеки для застосунку.
     * <p>
     * Визначає правила доступу до ресурсів, вмикає підтримку CORS та додає JWT-фільтр для обробки аутентифікації.
     *
     * @param http об'єкт {@link HttpSecurity}, який надає API для налаштування безпеки HTTP-запитів.
     * @return об'єкт {@link SecurityFilterChain}, що визначає ланцюг фільтрів безпеки.
     * @throws Exception якщо виникає помилка під час налаштування.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }

    /**
     * Налаштування CORS для застосунку.
     * <p>
     * Налаштовує дозволені джерела (origins), методи, заголовки та доступність облікових даних (credentials) для запитів.
     *
     * @return об'єкт {@link CorsConfigurationSource}, що містить конфігурацію CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Встановлює дозволені джерела для CORS-запитів
        configuration.setAllowedOrigins(List.of(frontendUrl));

        // Встановлює дозволені HTTP-методи для запитів
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Встановлює дозволені заголовки для запитів
        configuration.setAllowedHeaders(List.of("*"));

        // Додає заголовок "Authorization" до списку відкритих заголовків
        configuration.addExposedHeader("Authorization");

        // Дозволяє передачу облікових даних (cookies, токени тощо)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Створює об'єкт {@link BCryptPasswordEncoder} для шифрування паролів.
     * <p>
     * Використовує алгоритм шифрування BCrypt.
     *
     * @return об'єкт {@link BCryptPasswordEncoder}.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Бін менеджера аутентифікації.
     * Забезпечує доступ до AuthenticationManager, який використовується для перевірки облікових даних користувача.
     *
     * @param config Конфігурація аутентифікації Spring Security.
     * @return Менеджер аутентифікації.
     * @throws Exception якщо виникає помилка під час створення.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}