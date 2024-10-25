package servicebook.user.confirmation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import servicebook.requests.confirmation.ConfirmationRequest;

import servicebook.utils.responce.ErrorResponse;

import java.util.Map;

@RestController
@RequestMapping("/confirmation")
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    @Autowired
    public EmailConfirmationController(EmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmationRequest confirmationRequest) {
        if (emailConfirmationService.confirmEmail(confirmationRequest.getKey())) {
            return ResponseEntity.ok().body(Map.of("result", "success"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("key_is_missing"));
    }
}
