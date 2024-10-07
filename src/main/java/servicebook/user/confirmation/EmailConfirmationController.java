package servicebook.user.confirmation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import servicebook.user.confirmation.request.ConfirmationRequest;

import java.util.Map;

@RestController
@RequestMapping("/confirmation")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    @Autowired
    public EmailConfirmationController(EmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmationRequest confirmationRequest) {
        if (emailConfirmationService.confirmEmail(confirmationRequest.getKey())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().body(Map.of("message", "key_is_missing"));
    }
}
