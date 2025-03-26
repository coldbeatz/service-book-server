package servicebook.controllers.car.converters;

import org.springframework.core.convert.converter.Converter;

import org.springframework.stereotype.Component;

import servicebook.entity.engine.FuelType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListFuelTypesConverter implements Converter<String, List<FuelType>> {

    @Override
    public List<FuelType> convert(String source) {
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(value -> Enum.valueOf(FuelType.class, value))
                .collect(Collectors.toList());
    }
}
