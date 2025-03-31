package servicebook.user.confirmation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.requests.confirmation.ConfirmationRequest;

import servicebook.response.LoginResponse;

import servicebook.services.jwt.JwtService;

import servicebook.user.User;

import servicebook.utils.responce.ErrorResponse;

import java.util.Optional;

@RestController
@RequestMapping("/confirmation")
@RequiredArgsConstructor
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    private final JwtService jwtService;

    @PostMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmationRequest request) {
        Optional<EmailConfirmation> confirmation = emailConfirmationService.findByUniqueKey(request.getKey());

        if (confirmation.isPresent() && emailConfirmationService.confirmEmail(confirmation)) {
            User user = confirmation.get().getUser();
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok().body(new LoginResponse(user.getEmail(), token));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("key_is_missing"));
    }
}
