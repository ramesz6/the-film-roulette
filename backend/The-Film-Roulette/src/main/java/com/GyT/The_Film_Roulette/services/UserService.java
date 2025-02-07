package com.GyT.The_Film_Roulette.services;

import java.util.List;

import com.GyT.The_Film_Roulette.models.User;

public interface UserService {
    User addUsers(User user);

    List<User> getUsers();

    User updateUser(User user, Long id);

    User getUserById(Long id);

    void deleteUser(Long id);

}
