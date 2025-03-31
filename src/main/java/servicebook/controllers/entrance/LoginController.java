package servicebook.controllers.entrance;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.exceptions.UnauthorizedException;

import servicebook.requests.login.LoginRequest;

import servicebook.requests.login.TokenValidationRequest;

import servicebook.response.LoginResponse;

import servicebook.response.SuccessResponse;
import servicebook.services.jwt.JwtService;

import servicebook.user.User;
import servicebook.user.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /**
     * Валідація JWT токена (використовується для перевірки збереженої сесії на клієнті)
     */
    @PostMapping("/validation")
    public ResponseEntity<SuccessResponse> tokenValidation(@RequestBody TokenValidationRequest request) {
        String email = request.getEmail();
        String token = request.getToken();

        User user = userService.findUserByEmail(email).orElse(null);

        if (!jwtService.isTokenValid(token, user)) {
            throw new UnauthorizedException("invalid_token", "Incorrect token or email");
        }

        return ResponseEntity.ok(new SuccessResponse("token_valid"));
    }

    /**
     * Вхід користувача (автентифікація по email + пароль)
     */
    @PostMapping
    public ResponseEntity<LoginResponse> onLogin(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = userService.findUserByEmail(email).orElse(null);

        if (user == null || !userService.checkUserPassword(user, password)) {
            throw new UnauthorizedException("incorrect_login_or_password", "Incorrect login or password");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(user.getEmail(), token));
    }
}
