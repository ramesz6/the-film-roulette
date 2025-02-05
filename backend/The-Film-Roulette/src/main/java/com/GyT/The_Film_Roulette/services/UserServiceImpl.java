package com.GyT.The_Film_Roulette.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.exceptions.EmailAlreadyTakenException;
import com.GyT.The_Film_Roulette.exceptions.UserNotFoundException;
import com.GyT.The_Film_Roulette.models.User;
import com.GyT.The_Film_Roulette.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addUsers(User user) {
        if (userAlreadyExists(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail() + " is already taken");
        }
        return userRepository.save(user);
    }

    private boolean userAlreadyExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user, Long id) {
        return userRepository.findById(id).map(us -> {
            us.setEmail(user.getEmail());
            us.setUsername(user.getUsername());
            return userRepository.save(us);
        }).orElseThrow(() -> new UserNotFoundException("Cannot find user"));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with the Id :" + id));
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot find user with the Id :" + id);
        }
        userRepository.deleteById(id);
    }
}
