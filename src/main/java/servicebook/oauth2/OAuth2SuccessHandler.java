package servicebook.oauth2;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Lazy;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import servicebook.localization.Localization;

import servicebook.services.jwt.JwtService;

import servicebook.user.User;
import servicebook.user.UserService;
import servicebook.user.role.Role;

import java.io.IOException;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Objects;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final UserService userService;

    @Value("${app.frontend.oauth2-redirect}")
    private String oauth2Redirect;

    public OAuth2SuccessHandler(JwtService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private User registerUser(String email, String name, String sub, String picture) {
        User user = new User();

        user.setEmail(email);
        user.setFullName(name);
        user.setRole(Role.USER);
        user.setLocalization(Localization.EN);
        user.setConfirmEmail(true);
        user.setExternalProviderId(sub);
        user.setProvider(AuthProvider.GOOGLE);
        user.setProfilePictureUrl(picture);

        userService.saveOrUpdate(user);
        return user;
    }

    private void updateUser(User user, String sub, String picture) {
        boolean update = false;

        if (!user.isConfirmEmail()) {
            user.setConfirmEmail(true);
            update = true;
        }

        if (!Objects.equals(user.getExternalProviderId(), sub)) {
            user.setExternalProviderId(sub);
            update = true;
        }

        if (!Objects.equals(user.getProvider(), AuthProvider.GOOGLE)) {
            user.setProvider(AuthProvider.GOOGLE);
            update = true;
        }

        if (!Objects.equals(user.getProfilePictureUrl(), picture)) {
            user.setProfilePictureUrl(picture);
            update = true;
        }

        if (update) {
            userService.saveOrUpdate(user);
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub");
        String picture = oAuth2User.getAttribute("picture");

        User user = userService.findUserByEmail(email).orElse(null);

        if (user == null) {
            user = registerUser(email, name, sub, picture);
        } else {
            updateUser(user, sub, picture);
        }

        String token = jwtService.generateToken(user);

        log.info("OAuth2 login success: {}", email);

        response.sendRedirect(oauth2Redirect +
                "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) +
                "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
    }
}
