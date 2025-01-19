package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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

import servicebook.utils.responce.ErrorResponse;
import servicebook.utils.responce.ResponseUtil;
import servicebook.utils.responce.SuccessResponse;

import java.io.IOException;

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

                long longId = Long.parseLong(countryId);
                Optional<Country> country = countryRepository.findById(longId);

                CarBrand carBrand = new CarBrand();

                carBrand.setBrand(brand);
                carBrand.setCountry(country.orElse(null));
                carBrand.setImageResource(resource);

                carBrandRepository.save(carBrand);
            } else {
                throw new ClientException("resource_loading_error", "Error loading resource to remote hosting");
            }
        } catch (IOException e) {
            return ResponseUtil.error();
        } catch (ClientException e) {
            return ResponseUtil.error(e);
        }

        return ResponseEntity.ok(new SuccessResponse());
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
