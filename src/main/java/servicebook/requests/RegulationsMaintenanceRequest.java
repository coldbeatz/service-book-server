package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.entity.CarTransmissionType;
import servicebook.entity.engine.FuelType;

import java.util.List;

@Getter
@Setter
@ToString
public class RegulationsMaintenanceRequest {

    private LocalizedRequest workDescription;

    private boolean useDefault;

    private List<CarTransmissionType> transmissions;
    private List<FuelType> fuelTypes;

    private List<RegulationsMaintenanceTaskRequest> tasks;

    private Long carId;
}
