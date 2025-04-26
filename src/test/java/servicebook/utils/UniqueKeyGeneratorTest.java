package servicebook.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UniqueKeyGeneratorTest {

    @Test
    void shouldGenerateKeyOfCorrectLength() {
        String key = UniqueKeyGenerator.generateUniqueKey();

        assertEquals(16, key.length(), "Довжина ключа має бути 16 символів");
    }

    @Test
    void shouldGenerateAlphanumericKey() {
        String key = UniqueKeyGenerator.generateUniqueKey();

        assertTrue(key.matches("^[A-Za-z0-9]+$"), "Ключ має містити тільки латинські літери та цифри");
    }

    @Test
    void shouldGenerateDifferentKeysEachTime() {
        String a = UniqueKeyGenerator.generateUniqueKey();
        String b = UniqueKeyGenerator.generateUniqueKey();

        assertNotEquals(a, b, "Кожен згенерований ключ має бути унікальним");
    }

    @Test
    void shouldNotGenerateNullOrEmptyKey() {
        String key = UniqueKeyGenerator.generateUniqueKey();

        assertNotNull(key, "Ключ не повинен бути null");
        assertFalse(key.isEmpty(), "Ключ не повинен бути порожнім");
    }
}