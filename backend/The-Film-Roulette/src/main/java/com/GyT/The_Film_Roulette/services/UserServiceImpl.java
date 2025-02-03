package com.GyT.The_Film_Roulette.services;

import lombok.*;
import org.springframework.stereotype.Service;
import com.GyT.The_Film_Roulette.repositories.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo; 
    // legyen jo    
}
