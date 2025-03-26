package servicebook.controllers.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import servicebook.controllers.BaseController;

import servicebook.entity.UserCar;
import servicebook.entity.UserCarNote;

import servicebook.requests.UserCarNoteRequest;

import servicebook.services.HtmlContentService;
import servicebook.services.UserCarNoteService;
import servicebook.services.UserCarService;

import servicebook.user.User;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;

/**
 * REST-контролер для керування нотатками автомобіля користувача.
 * Доступно авторизованим користувачам
 */
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/user/cars/{userCarId}/notes")
@RequiredArgsConstructor
public class UserCarNoteController extends BaseController {

    private final UserCarService userCarService;
    private final UserCarNoteService userCarNoteService;

    private final HtmlContentService htmlContentService;

    /**
     * Перевірка чи користувач має доступ до запису нотатки автомобіля
     */
    private void accessValidation(UserCarNote note, Long userCarId) {
        UserCar userCar = note.getUserCar();

        if (!Objects.equals(userCar.getId(), userCarId)) {
            throw new AccessDeniedException("You do not have permission to access this user car");
        }

        accessValidation(userCar);
    }

    /**
     * Перевірка чи користувач має доступ до автомобіля
     */
    private void accessValidation(UserCar userCar) {
        User creator = userCar.getUser();
        User authUser = getAuthenticatedUser();

        if (!Objects.equals(creator.getId(), authUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this car");
        }
    }

    /**
     * Отримати нотатки автомобіля
     */
    @GetMapping
    public ResponseEntity<List<UserCarNote>> findAll(@PathVariable("userCarId") Long userCarId) {
        UserCar userCar = userCarService.getById(userCarId);
        accessValidation(userCar);

        List<UserCarNote> notes = userCarNoteService.findByUserCar(userCar);

        return ResponseEntity.ok(notes);
    }

    /**
     * Отримати нотатку за ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserCarNote> findById(@PathVariable("userCarId") Long userCarId,
                                                @PathVariable("id") Long id) {

        UserCarNote note = userCarNoteService.getById(id);
        accessValidation(note, userCarId);

        return ResponseEntity.ok(note);
    }

    /**
     * Видалення нотатки за ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("userCarId") Long userCarId,
                                       @PathVariable Long id) {

        UserCarNote note = userCarNoteService.getById(id);
        accessValidation(note, userCarId);

        userCarNoteService.delete(note);

        return ResponseEntity.noContent().build();
    }

    /**
     * Створення нової нотатки для автомобіля користувача
     */
    @PostMapping
    public ResponseEntity<UserCarNote> save(@PathVariable("userCarId") Long userCarId,
                                            @RequestBody UserCarNoteRequest request) {

        UserCar userCar = userCarService.getById(userCarId);
        accessValidation(userCar);

        UserCarNote note = new UserCarNote();

        note.setUserCar(userCar);
        note.setCreatedAt(LocalDateTime.now());

        saveOrUpdate(note, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    /**
     * Оновлення нотатки
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserCarNote> update(@PathVariable("userCarId") Long userCarId,
                                              @PathVariable("id") Long id,
                                              @RequestBody UserCarNoteRequest request) {

        UserCarNote note = userCarNoteService.getById(id);
        accessValidation(note, userCarId);

        note.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(note, request);
        return ResponseEntity.ok(note);
    }

    /**
     * Спільний метод для заповнення та збереження нотатки
     */
    private void saveOrUpdate(UserCarNote note, UserCarNoteRequest request) {
        User user = getAuthenticatedUser();

        String prepareContent = htmlContentService.processContent(request.getContent(), user);

        note.setContent(prepareContent);
        note.setShortDescription(request.getShortDescription());

        userCarNoteService.saveOrUpdate(note);
    }
}
