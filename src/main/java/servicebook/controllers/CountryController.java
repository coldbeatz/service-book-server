package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.entity.Country;

import servicebook.services.CountryService;

import java.util.List;

/**
 * REST-контролер для роботи з країнами.
 * <p>
 * Доступний лише адміністраторам (ROLE_ADMIN).
 * Забезпечує отримання списку всіх країн, які можуть використовуватися
 * для вибору країни при додаванні бренду автомобіля
 * <p>
 * Всі запити обробляються на шляху /countries.
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    /**
     * Отримати список усіх країн
     *
     * @return ResponseEntity зі списком країн
     */
    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = countryService.getAll();
        return ResponseEntity.ok(countries);
    }
}
