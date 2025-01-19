package servicebook.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import servicebook.user.User;

public class BaseController {

    protected User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();
        }

        throw new RuntimeException("User is not authenticated");
    }
}
