package servicebook.controllers.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;

import servicebook.controllers.BaseController;

import servicebook.exceptions.BadRequestException;

import servicebook.exceptions.UnauthorizedException;
import servicebook.localization.Localization;

import servicebook.requests.RegistrationRequest;
import servicebook.requests.login.LoginRequest;
import servicebook.requests.login.TokenValidationRequest;

import servicebook.response.LoginResponse;
import servicebook.response.SuccessResponse;

import servicebook.services.jwt.JwtService;
import servicebook.services.mail.EmailService;

import servicebook.user.User;
import servicebook.user.UserService;
import servicebook.user.role.Role;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService userService;

    private final JwtService jwtService;

    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    /**
     * Отримання авторизованого користувача
     */
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<User> getAuthUser() {
        User user = getAuthenticatedUser();

        return ResponseEntity.ok(user);
    }

    /**
     * Реєстрація користувача
     */
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        if (!emailService.isValid(request.getEmail())) {
            throw new BadRequestException("invalid_email", "Email is invalid");
        }

        User user = userService
                .findUserByEmail(request.getEmail())
                .orElse(new User());

        if (user.isConfirmEmail()) {
            throw new BadRequestException("email_exists", "Email is already registered");
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);
        user.setLocalization(Localization.EN);

        userService.setPassword(user, request.getPassword());
        userService.saveOrUpdate(user);

        emailService.sendRegistrationConfirmationEmail(user);

        return ResponseEntity.ok(new SuccessResponse());
    }

    /**
     * Вхід користувача (автентифікація по email + пароль)
     */
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> onLogin(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = userService.findUserByEmail(email).orElse(null);

        if (password == null) {
            throw new BadRequestException("incorrect_login_or_password", "Password is invalid");
        }

        if (user == null || !userService.checkUserPassword(user, password)) {
            throw new UnauthorizedException("incorrect_login_or_password", "Incorrect login or password");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(user.getEmail(), token));
    }

    /**
     * Валідація JWT токена (використовується для перевірки збереженої сесії на клієнті)
     */
    @PostMapping("/token/validation")
    public ResponseEntity<SuccessResponse> tokenValidation(@RequestBody TokenValidationRequest request) {
        String email = request.getEmail();
        String token = request.getToken();

        User user = userService.findUserByEmail(email).orElse(null);

        if (!jwtService.isTokenValid(token, user)) {
            throw new UnauthorizedException("invalid_token", "Incorrect token or email");
        }

        return ResponseEntity.ok(new SuccessResponse("token_valid"));
    }
}