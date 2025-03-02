package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.CarTransmissionType;

import servicebook.entity.engine.FuelType;

@Getter
@Setter
@ToString
public class UserCarRequest {

    private Long carId;
    private Long engineId;

    private int vehicleYear;

    private String vinCode;

    private CarTransmissionType transmissionType;
    private FuelType fuelType;

    private int vehicleMileage;

    private MultipartFile file;

    @SuppressWarnings("unused")
    public void setTransmissionType(String transmissionType) {
        this.transmissionType = CarTransmissionType.valueOf(transmissionType);
    }

    @SuppressWarnings("unused")
    public void setFuelType(String fuelType) {
        this.fuelType = FuelType.valueOf(fuelType);
    }
}
