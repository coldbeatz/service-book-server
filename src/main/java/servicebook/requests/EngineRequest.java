package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.entity.engine.FuelType;

@Getter
@Setter
@ToString
public class EngineRequest {

    private String name;

    private double displacement;
    private int horsepower;

    private FuelType fuelType;
}
