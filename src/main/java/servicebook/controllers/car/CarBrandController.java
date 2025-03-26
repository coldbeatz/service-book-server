package servicebook.controllers.car;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.controllers.BaseController;
import servicebook.entity.CarBrand;
import servicebook.entity.Country;

import servicebook.requests.BrandRequest;

import servicebook.resources.Resource;

import servicebook.services.CarBrandService;
import servicebook.services.CarService;
import servicebook.services.CountryService;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

/**
 * REST-контролер для керування брендами автомобілів.
 * Доступно адміністраторам (шлях /brands).
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class CarBrandController extends BaseController {

    private final CarBrandService carBrandService;
    private final CarService carService;

    private final CountryService countryService;

    private final FileUploadService fileUploadService;

    /**
     * Отримати кількість автомобілів, які належать до певного бренду.
     *
     * @param brandId Ідентифікатор бренду
     * @return Кількість автомобілів
     */
    @GetMapping("/{id}/cars/count")
    public ResponseEntity<Map<String, Long>> getCarsCountByBrandId(@PathVariable("id") Long brandId) {
        long carsCount = carService.getCarsCountByBrandId(brandId);

        return ResponseEntity.ok(Map.of("count", carsCount));
    }

    /**
     * Отримати бренд за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarBrand> findById(@PathVariable("id") Long id) {
        CarBrand carBrand = carBrandService.findById(id);

        return ResponseEntity.ok(carBrand);
    }

    /**
     * Отримати список усіх брендів
     */
    @GetMapping
    public ResponseEntity<List<CarBrand>> getAllBrands() {
        List<CarBrand> carBrands = carBrandService.getAll();

        return ResponseEntity.ok(carBrands);
    }

    /**
     * Створити новий бренд
     */
    @PostMapping
    public ResponseEntity<CarBrand> save(@ModelAttribute BrandRequest request) {
        CarBrand brand = new CarBrand();

        brand.setCreatedBy(getAuthenticatedUser());
        brand.setCreatedAt(LocalDateTime.now());

        saveOrUpdate(brand, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    /**
     * Оновити існуючий бренд
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarBrand> updateBrand(@PathVariable Long id, @ModelAttribute BrandRequest request) {
        CarBrand brand = carBrandService.findById(id);

        brand.setUpdatedBy(getAuthenticatedUser());
        brand.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(brand, request);
        return ResponseEntity.ok(brand);
    }

    private void saveOrUpdate(CarBrand carBrand, BrandRequest request) {
        User user = getAuthenticatedUser();

        MultipartFile imageFile = request.getImageFile();
        Resource resource = carBrand.getImageResource();

        if (imageFile != null && !imageFile.isEmpty()) {
            if (carBrand.getId() == null) {
                resource = fileUploadService.uploadFileToExternalHost(imageFile, user);
            } else {
                resource = fileUploadService.uploadFileToExternalHost(imageFile, user, resource);
            }

            carBrand.setImageResource(resource);
        }

        Country country = countryService.getById(request.getCountryId());

        carBrand.setBrand(request.getBrand());
        carBrand.setCountry(country);

        carBrandService.saveOrUpdate(carBrand);
    }
}
