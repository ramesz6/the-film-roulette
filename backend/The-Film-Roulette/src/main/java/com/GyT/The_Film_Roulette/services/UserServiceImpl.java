package com.GyT.The_Film_Roulette.services;

import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    public void deleteProfileByName(String name) {
        userRepository.deleteByUsername(name);
    }
}
