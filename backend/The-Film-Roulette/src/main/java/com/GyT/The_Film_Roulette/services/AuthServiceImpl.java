package com.GyT.The_Film_Roulette.services;

import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.dtos.login.LoginRequest;
import com.GyT.The_Film_Roulette.dtos.login.LoginResponse;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;
import com.GyT.The_Film_Roulette.dtos.register.RegisterResponse;

@Service
class AuthServiceImpl implements AuthService {

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    
}