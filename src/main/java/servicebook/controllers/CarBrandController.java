package servicebook.controllers;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.CarBrand;
import servicebook.entity.Country;

import servicebook.resources.Resource;

import servicebook.services.CarBrandService;
import servicebook.services.CarService;
import servicebook.services.CountryService;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/brands")
@RequiredArgsConstructor
public class CarBrandController extends BaseController {

    private final CarBrandService carBrandService;
    private final CarService carService;
    private final CountryService countryService;

    private final FileUploadService fileUploadService;

    @GetMapping("/{id}/cars/count")
    public ResponseEntity<?> getCarsCountByBrandId(@PathVariable("id") Long brandId) {
        long carsCount = carService.getCarsCountByBrandId(brandId);
        return ResponseUtil.success(Map.of("count", carsCount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        try {
            CarBrand carBrand = carBrandService.findById(id);

            return ResponseUtil.success(carBrand);
        } catch (EntityNotFoundException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "car_brand_not_found");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBrands() {
        try {
            List<CarBrand> carBrands = carBrandService.getAll();
            return ResponseUtil.success(carBrands);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id,
                                         @RequestParam("brand") String brand,
                                         @RequestParam(value = "countryId", required = false) Long countryId,
                                         @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            User user = getAuthenticatedUser();

            CarBrand carBrand = carBrandService.findById(id);
            Resource resource = carBrand.getImageResource();

            if (file != null && !file.isEmpty()) {
                resource = fileUploadService.uploadFileToExternalHost(file, user, resource);
                carBrand.setImageResource(resource);
            }

            Country country = null;
            if (countryId != null) {
                country = countryService.getById(countryId);
            }

            carBrand.setBrand(brand);
            carBrand.setCountry(country);

            carBrandService.saveOrUpdate(carBrand);

            return ResponseEntity.ok(carBrand);
        } catch (EntityNotFoundException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "car_brand_not_found");
        }
    }

    @PostMapping
    public ResponseEntity<?> saveBrand(@RequestParam("brand") String brand,
                                       @RequestParam(value = "countryId", required = false) Long countryId,
                                       @RequestParam("file") MultipartFile file) {

        User user = getAuthenticatedUser();

        Resource resource = null;
        if (file != null && !file.isEmpty()) {
            resource = fileUploadService.uploadFileToExternalHost(file, user);
        }

        Country country = null;
        if (countryId != null) {
            country = countryService.getById(countryId);
        }

        CarBrand carBrand = CarBrand.builder()
                .brand(brand)
                .country(country)
                .imageResource(resource)
                .build();

        carBrandService.saveOrUpdate(carBrand);

        return ResponseEntity.ok(carBrand);
    }
}
