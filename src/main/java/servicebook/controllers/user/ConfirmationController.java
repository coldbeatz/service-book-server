package servicebook.controllers.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.exceptions.BadRequestException;

import servicebook.requests.confirmation.ConfirmationRequest;

import servicebook.response.SuccessResponse;

import servicebook.user.confirmation.EmailConfirmation;
import servicebook.user.confirmation.EmailConfirmationService;

import java.util.Optional;

@RestController
@RequestMapping("/user/confirmation")
@RequiredArgsConstructor
public class ConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    @PostMapping
    public ResponseEntity<SuccessResponse> confirm(@RequestBody ConfirmationRequest request) {
        Optional<EmailConfirmation> confirmation = emailConfirmationService.findByUniqueKey(request.getKey());

        if (confirmation.isPresent() && emailConfirmationService.confirmEmail(confirmation)) {
            return ResponseEntity.ok().body(new SuccessResponse("confirmation_success"));
        }

        throw new BadRequestException("key_is_missing", "Confirmation key is missing");
    }
}
