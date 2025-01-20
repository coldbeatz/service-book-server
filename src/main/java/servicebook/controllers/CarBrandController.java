package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.CarBrand;
import servicebook.entity.Country;

import servicebook.exceptions.ClientException;
import servicebook.repository.CarBrandRepository;
import servicebook.repository.CountryRepository;

import servicebook.repository.ResourceRepository;

import servicebook.resources.Resource;

import servicebook.services.upload.FileUploadResponse;
import servicebook.services.upload.FileUploadService;
import servicebook.services.upload.FileUploadStatus;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;
import servicebook.utils.responce.SuccessResponse;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("admin/brands")
@RequiredArgsConstructor
public class CarBrandController extends BaseController {

    private final CarBrandRepository carBrandRepository;
    private final CountryRepository countryRepository;
    private final ResourceRepository resourceRepository;

    private final FileUploadService fileUploadService;

    @PostMapping("/edit")
    public ResponseEntity<?> createBrand(@RequestParam("id") String brandId,
                                         @RequestParam("brand") String brand,
                                         @RequestParam(value = "countryId", required = false) String countryId,
                                         @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            User user = getAuthenticatedUser();

            Optional<CarBrand> carBrandOptional = carBrandRepository.findById(Long.parseLong(brandId));
            if (carBrandOptional.isEmpty()) {
                return ResponseUtil.error("car_brand_not_found");
            }

            CarBrand carBrand = carBrandOptional.get();
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
                }

                resourceRepository.save(resource);
            }

            Country country = null;
            if (countryId != null) {
                country = countryRepository.findById(Long.parseLong(countryId)).orElse(null);
            }

            carBrand.setBrand(brand);
            carBrand.setCountry(country);
            carBrand.setImageResource(resource);

            carBrandRepository.save(carBrand);

            return ResponseEntity.ok(new SuccessResponse());
        } catch (IOException e) {
            return ResponseUtil.error();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBrand(@RequestParam("brand") String brand,
                                         @RequestParam(value = "countryId", required = false) String countryId,
                                         @RequestParam("file") MultipartFile file) {
        try {
            User user = getAuthenticatedUser();

            FileUploadResponse result = fileUploadService.uploadFileToExternalHost(file);
            FileUploadStatus status = result.getStatus();

            if (status == FileUploadStatus.SUCCESS) {
                Resource resource = Resource.createResource(result.getFileName(), user);
                resourceRepository.save(resource);

                Country country = null;
                if (countryId != null) {
                    country = countryRepository.findById(Long.parseLong(countryId)).orElse(null);
                }

                CarBrand carBrand = new CarBrand();

                carBrand.setBrand(brand);
                carBrand.setCountry(country);
                carBrand.setImageResource(resource);

                carBrandRepository.save(carBrand);
            } else {
                throw new ClientException("resource_loading_error", "Error loading resource to remote hosting");
            }

            return ResponseEntity.ok(new SuccessResponse());
        } catch (IOException e) {
            return ResponseUtil.error();
        } catch (ClientException e) {
            return ResponseUtil.error(e);
        }
    }

    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountries() {
        try {
            List<Country> countries = StreamSupport
                    .stream(countryRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            return ResponseUtil.success(countries);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        try {
            Optional<CarBrand> carBrand = carBrandRepository.findById(id);

            if (carBrand.isPresent()) {
                return ResponseUtil.success(carBrand.get());
            }

            return ResponseUtil.error("car_brand_not_found");
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        try {
            List<CarBrand> carBrands = StreamSupport
                    .stream(carBrandRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            return ResponseUtil.success(carBrands);
        } catch (Exception e) {
            return ResponseUtil.error();
        }
    }
}
