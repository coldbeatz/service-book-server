package servicebook.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Тип трансмісій автомобіля
 */
public enum CarTransmissionType {
    /**
     * Механічна коробка передач
     */
    MANUAL,

    /**
     * Автоматична коробка передач
     */
    AUTOMATIC,

    /**
     * Варіаторна трансмісія
     */
    CVT,

    /**
     * Роботизована коробка передач
     */
    SEMI_AUTOMATIC,

    /**
     * Електропривод
     */
    ELECTRIC_DRIVE,

    /**
     * Інша трансмісія
     */
    OTHER;

    @JsonValue
    public int toOrdinal() {
        return this.ordinal();
    }

    @JsonCreator
    public static CarTransmissionType fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal for CarTransmissionType: " + ordinal);
        }
        return values()[ordinal];
    }
}
