package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class BrandRequest {

    /**
     * Назва бренду
     */
    private String brand;

    /**
     * Картинка бренду
     */
    private MultipartFile imageFile;

    /**
     * Країна бренду
     */
    private Long countryId;
}
