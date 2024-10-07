package servicebook.utils;

import java.security.SecureRandom;

public class UniqueKeyGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int KEY_LENGTH = 16;

    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueKey() {
        StringBuilder uniqueKey = new StringBuilder(KEY_LENGTH);

        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            uniqueKey.append(CHARACTERS.charAt(index));
        }

        return uniqueKey.toString();
    }
}
