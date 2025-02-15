package servicebook.entity.news;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Тип постингу новин
 */
public enum NewsPostingOption {
    /**
     * Новина на сайті
     */
    WEBSITE,
    /**
     * Новина відправляється користувачеві на пошту
     */
    EMAIL;

    @JsonValue
    public int toOrdinal() {
        return this.ordinal();
    }

    @JsonCreator
    public static NewsPostingOption fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal for NewsPostingOption: " + ordinal);
        }
        return values()[ordinal];
    }
}
