package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.entity.maintenance.MaintenanceWorkType;

@Getter
@Setter
@ToString
public class RegulationsMaintenanceTaskRequest {

    private long id;

    private int interval;
    private int specificMileage;

    private MaintenanceWorkType workType;
}
