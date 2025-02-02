package servicebook.controllers.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import servicebook.entity.CarTransmissionType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListTransmissionsConverter implements Converter<String, List<CarTransmissionType>> {

    @Override
    public List<CarTransmissionType> convert(String source) {
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(value -> Enum.valueOf(CarTransmissionType.class, value))
                .collect(Collectors.toList());
    }
}
