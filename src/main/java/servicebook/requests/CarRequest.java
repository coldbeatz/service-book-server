package servicebook.requests;

import lombok.Getter;
import lombok.Setter;

import lombok.ToString;

import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.CarTransmissionType;

import java.util.List;

@Getter
@Setter
@ToString
public class CarRequest {

    private Long brandId;

    private String model;

    private Integer startYear;
    private Integer endYear;

    private MultipartFile file;

    private List<CarTransmissionType> transmissions;

    @SuppressWarnings("unused")
    public void setTransmissions(List<String> transmissions) {
        this.transmissions = transmissions.stream()
                .map(CarTransmissionType::valueOf)
                .toList();
    }
}
