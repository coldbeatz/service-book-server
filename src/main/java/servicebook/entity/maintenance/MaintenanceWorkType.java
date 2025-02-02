package servicebook.entity.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Тип робіт обслуговування
 * При додаванні або заміні типів редагувати на клієнті також
 */
public enum MaintenanceWorkType {
    /**
     * Перевірка
     */
    INSPECTION,
    /**
     * Заміна
     */
    REPLACEMENT;

    @JsonValue
    public int toOrdinal() {
        return this.ordinal();
    }

    @JsonCreator
    public static MaintenanceWorkType fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal for MaintenanceWorkType: " + ordinal);
        }
        return values()[ordinal];
    }
}
