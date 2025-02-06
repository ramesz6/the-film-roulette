package com.GyT.The_Film_Roulette.services.auth.jwt;

import java.security.Key;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.configurations.JwtConfiguration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class JwtServiceImpl implements JwtService {

    private final JwtConfiguration jwtConfiguration;

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .signWith(getSignInKey())
                .compact();
        
                
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfiguration.jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(UserDetails userDetails, Map<String, Objects> additional) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateToken'");
    }

}