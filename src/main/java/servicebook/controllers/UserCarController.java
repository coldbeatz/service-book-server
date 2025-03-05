package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import servicebook.entity.Car;
import servicebook.entity.UserCar;
import servicebook.entity.UserCarNote;
import servicebook.entity.engine.CarEngine;

import servicebook.requests.UserCarRequest;
import servicebook.resources.Resource;

import servicebook.services.CarEngineService;
import servicebook.services.CarService;
import servicebook.services.UserCarNoteService;
import servicebook.services.UserCarService;

import servicebook.services.upload.FileUploadService;

import servicebook.user.User;

import servicebook.utils.responce.ResponseUtil;

import java.time.LocalDateTime;

import java.util.List;

@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/user/cars")
@RequiredArgsConstructor
public class UserCarController extends BaseController {

    private final UserCarService userCarService;

    private final CarService carService;
    private final CarEngineService carEngineService;
    private final UserCarNoteService userCarNoteService;

    private final FileUploadService fileUploadService;

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<UserCarNote>> getNotes(@PathVariable Long id) {
        UserCar userCar = userCarService.getById(id);

        User creator = userCar.getCreatedBy();
        User authUser = getAuthenticatedUser();

        if (creator.getId() != authUser.getId())
            throw new AccessDeniedException("You do not have permission to access this car");

        List<UserCarNote> notes = userCarNoteService.findByUserCar(userCar);

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCar> getUserCarById(@PathVariable Long id) {
        UserCar userCar = userCarService.getById(id);

        return ResponseUtil.success(userCar);
    }

    @GetMapping
    public ResponseEntity<List<UserCar>> getUserCars() {
        List<UserCar> userCars = userCarService.getAll();

        return ResponseEntity.ok(userCars);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserCar userCar = userCarService.getById(id);
        userCarService.delete(userCar);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserCar> save(@ModelAttribute UserCarRequest request) {
        User user = getAuthenticatedUser();

        UserCar userCar = new UserCar();
        userCar.setCreatedBy(user);

        saveOrUpdateUserCar(userCar, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @ModelAttribute UserCarRequest request) {
        User user = getAuthenticatedUser();
        UserCar userCar = userCarService.getById(id);

        userCar.setUpdatedBy(user);
        userCar.setUpdatedAt(LocalDateTime.now());

        saveOrUpdateUserCar(userCar, request);
        return ResponseEntity.ok(userCar);
    }

    private void saveOrUpdateUserCar(UserCar userCar, UserCarRequest request) {
        User user = getAuthenticatedUser();
        MultipartFile file = request.getFile();

        if (file != null && !file.isEmpty()) {
            Resource resource = fileUploadService.uploadFileToExternalHost(file, user, userCar.getImageResource());
            userCar.setImageResource(resource);
        }

        Car car = carService.getCarById(request.getCarId());
        userCar.setCar(car);

        if (request.getEngineId() != null) {
            CarEngine engine = carEngineService.getEngineById(request.getEngineId());
            userCar.setEngine(engine);
        }

        userCar.setVehicleMileage(request.getVehicleMileage());
        userCar.setVehicleYear(request.getVehicleYear());

        userCar.setVinCode(request.getVinCode());
        userCar.setLicensePlate(request.getLicensePlate());

        userCar.setTransmissionType(request.getTransmissionType());
        userCar.setFuelType(request.getFuelType());

        userCarService.saveOrUpdate(userCar);
    }
}
