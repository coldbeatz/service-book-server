package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.entity.Country;

import servicebook.services.CountryService;

import servicebook.utils.responce.ResponseUtil;

import java.util.List;

@RestController
@RequestMapping("admin/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<?> getAllCountries() {
        List<Country> countries = countryService.getAll();
        return ResponseUtil.success(countries);
    }
}
