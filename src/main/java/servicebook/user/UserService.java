package servicebook.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
