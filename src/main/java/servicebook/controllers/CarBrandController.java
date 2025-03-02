package servicebook.controllers;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.CarBrand;
import servicebook.entity.Country;

import servicebook.exceptions.ClientException;

import servicebook.resources.Resource;

import servicebook.services.CarBrandService;
import servicebook.services.CountryService;

import servicebook.services.upload.FileUploadResponse;
import servicebook.services.upload.FileUploadService;
import servicebook.services.upload.FileUploadStatus;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/brands")
@RequiredArgsConstructor
public class CarBrandController extends BaseController {

    private final CarBrandService carBrandService;
    private final CountryService countryService;

    private final FileUploadService fileUploadService;

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
                FileUploadResponse result = fileUploadService.uploadFileToExternalHost(file);
                FileUploadStatus status = result.getStatus();

                if (status != FileUploadStatus.SUCCESS) {
                    return ResponseUtil.error("resource_loading_error");
                }

                if (resource != null) {
                    resource.setUrl(result.getFileName());
                    resource.setUser(user);
                    resource.setUploadDate(LocalDateTime.now());
                } else {
                    resource = Resource.createResource(result.getFileName(), user);
                    carBrand.setImageResource(resource);
                }
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
        try {
            User user = getAuthenticatedUser();

            FileUploadResponse result = fileUploadService.uploadFileToExternalHost(file);
            FileUploadStatus status = result.getStatus();

            if (status == FileUploadStatus.SUCCESS) {
                Resource resource = Resource.createResource(result.getFileName(), user);

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
            } else {
                throw new ClientException("resource_loading_error", "Error loading resource to remote hosting");
            }
        } catch (ClientException e) {
            return ResponseUtil.error(e);
        }
    }
}
