import java.security.SecureRandom;
import java.util.Base64;

public class Test {

    public static void main(String[] args) {
        byte[] key = new byte[32]; // 256 бит = 32 байта
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Ваш jwtSecret: " + base64Key);
    }
}
