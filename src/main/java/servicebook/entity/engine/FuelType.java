package servicebook.entity.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Тип палива
 */
public enum FuelType {
    /**
     * Бензин
     */
    PETROL,

    /**
     * Дизель
     */
    DIESEL,

    /**
     * Електро
     */
    ELECTRIC,

    /**
     * Гібрид
     */
    HYBRID,

    /**
     * Газ
     */
    LPG,

    /**
     * Газ/Бензин (двопаливний)
     */
    LPG_PETROL,

    /**
     * Інші види палива
     */
    OTHER;

    @JsonValue
    public int toOrdinal() {
        return this.ordinal();
    }

    @JsonCreator
    public static FuelType fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal for FuelType: " + ordinal);
        }
        return values()[ordinal];
    }
}