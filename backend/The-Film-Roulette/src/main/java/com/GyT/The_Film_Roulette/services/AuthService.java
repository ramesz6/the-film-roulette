package com.GyT.The_Film_Roulette.services;

import com.GyT.The_Film_Roulette.dtos.login.LoginRequest;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;

public interface AuthService {

    Object register(RegisterRequest registerRequest);

    Object login(LoginRequest loginRequest);

}
