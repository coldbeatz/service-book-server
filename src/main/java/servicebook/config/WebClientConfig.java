package servicebook.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * URL зовнішнього сервера для завантаження файлів
     */
    @Value("${external.api.resource.server}")
    private String externalApiResourceServer;

    @Bean
    @Qualifier("externalApiWebClient")
    public WebClient externalApiWebClient() {
        return WebClient.builder()
                .baseUrl(externalApiResourceServer)
                .build();
    }
}