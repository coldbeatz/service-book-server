package servicebook.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.Country;

import servicebook.repository.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Country getById(Long id) {
        return countryRepository.findById(id).orElse(null);
    }
}
