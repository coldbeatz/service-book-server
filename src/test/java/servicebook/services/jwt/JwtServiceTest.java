package servicebook.services.jwt;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import servicebook.user.User;
import servicebook.user.role.Role;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private static final String TEST_EMAIL = "vbhrytsenko@gmail.com";

    @Test
    void shouldGenerateValidTokenAndValidate() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }

    @Test
    void shouldExtractEmailFromToken() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);
        String extractedEmail = jwtService.extractUserEmail(token);
        assertEquals(user.getEmail(), extractedEmail);
    }
}