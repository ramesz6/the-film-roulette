package com.GyT.The_Film_Roulette.services.auth.jwt;

import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(UserDetails userDetails);
    String generateToken(UserDetails userDetails, Map<String,Objects> additional);
    
}
