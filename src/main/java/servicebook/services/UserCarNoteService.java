package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.UserCar;
import servicebook.entity.UserCarNote;

import servicebook.repository.UserCarNoteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCarNoteService {

    private final UserCarNoteRepository userCarNoteRepository;

    @Transactional(readOnly = true)
    public List<UserCarNote> findByUserCar(UserCar userCar) {
        return userCarNoteRepository.findByUserCar(userCar);
    }

    @Transactional
    public void saveOrUpdate(UserCarNote note) {
        userCarNoteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public UserCarNote getById(Long id) {
        return userCarNoteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void delete(UserCarNote note) {
        userCarNoteRepository.delete(note);
    }
}
