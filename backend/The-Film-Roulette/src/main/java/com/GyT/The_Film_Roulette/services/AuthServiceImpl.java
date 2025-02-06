package com.GyT.The_Film_Roulette.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.dtos.login.LoginRequest;
import com.GyT.The_Film_Roulette.dtos.login.LoginResponse;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;
import com.GyT.The_Film_Roulette.dtos.register.RegisterResponse;
import com.GyT.The_Film_Roulette.models.User;
import com.GyT.The_Film_Roulette.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
       User newRegisterUser = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .build();
    userRepository.save(newRegisterUser);
    return new RegisterResponse();

    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    
}