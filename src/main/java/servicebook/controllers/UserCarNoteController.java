package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.UserCar;
import servicebook.entity.UserCarNote;

import servicebook.requests.UserCarNoteRequest;

import servicebook.services.HtmlContentService;
import servicebook.services.UserCarNoteService;
import servicebook.services.UserCarService;
import servicebook.user.User;

import java.time.LocalDateTime;

@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/user/cars/notes")
@RequiredArgsConstructor
public class UserCarNoteController extends BaseController {

    private final UserCarService userCarService;
    private final UserCarNoteService userCarNoteService;

    private final HtmlContentService htmlContentService;

    @GetMapping("/{id}")
    public ResponseEntity<UserCarNote> findById(@PathVariable("id") Long id) {
        UserCarNote note = userCarNoteService.getById(id);

        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserCarNote note = userCarNoteService.getById(id);
        userCarNoteService.delete(note);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserCarNote> save(@RequestBody UserCarNoteRequest request) {
        UserCarNote note = new UserCarNote();

        saveOrUpdate(note, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCarNote> update(@PathVariable("id") Long id, @RequestBody UserCarNoteRequest request) {
        UserCarNote note = userCarNoteService.getById(id);

        note.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(note, request);
        return ResponseEntity.ok(note);
    }

    private void saveOrUpdate(UserCarNote note, UserCarNoteRequest request) {
        User user = getAuthenticatedUser();
        UserCar userCar = userCarService.getById(request.getUserCarId());

        String prepareContent = htmlContentService.processContent(request.getContent(), user);

        note.setUserCar(userCar);
        note.setContent(prepareContent);
        note.setShortDescription(request.getShortDescription());

        userCarNoteService.saveOrUpdate(note);
    }
}
