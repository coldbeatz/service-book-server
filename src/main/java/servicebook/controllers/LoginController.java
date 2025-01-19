package servicebook.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import servicebook.exceptions.ClientException;

import servicebook.requests.login.LoginRequest;

import servicebook.requests.login.TokenValidationRequest;
import servicebook.response.LoginResponse;

import servicebook.services.jwt.JwtService;

import servicebook.user.User;
import servicebook.user.UserService;

import servicebook.utils.responce.ErrorResponse;
import servicebook.utils.responce.SuccessResponse;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/validation")
    public ResponseEntity<?> tokenValidation(@RequestBody TokenValidationRequest request) {
        try {
            String email = request.getEmail();
            String token = request.getToken();

            User user = userService.getUserByEmail(email);

            if (!jwtService.isTokenValid(token, user)) {
                throw new ClientException("invalid_token", "Incorrect token or email");
            }

            return ResponseEntity.ok(new SuccessResponse());
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }

    @PostMapping
    public ResponseEntity<?> onLogin(@RequestBody LoginRequest request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            User user = userService.getUserByEmail(email);

            if (!userService.checkUserPassword(user, password)) {
                throw new ClientException("incorrect_login_or_password", "Incorrect login or password");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
        }
    }
}
