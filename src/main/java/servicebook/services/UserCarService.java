package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.UserCar;

import servicebook.repository.UserCarRepository;
import servicebook.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCarService {

    private final UserCarRepository userCarRepository;

    @Transactional(readOnly = true)
    public List<UserCar> getAllByUser(User user) {
        return userCarRepository.findAllByUser(user);
    }

    @Transactional
    public void saveOrUpdate(UserCar userCar) {
        userCarRepository.save(userCar);
    }

    @Transactional(readOnly = true)
    public UserCar getById(Long id) {
        return userCarRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void delete(UserCar userCar) {
        userCarRepository.delete(userCar);
    }
}
