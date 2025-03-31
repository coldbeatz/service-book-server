package servicebook.controllers.exceptions;

import servicebook.exceptions.UnauthorizedException;

public class EmailNotConfirmedException extends UnauthorizedException {

    public EmailNotConfirmedException() {
        super("email_not_confirmed", "Email not confirmed");
    }
}
