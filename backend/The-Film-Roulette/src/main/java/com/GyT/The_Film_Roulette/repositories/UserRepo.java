package com.GyT.The_Film_Roulette.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GyT.The_Film_Roulette.models.*;


public interface UserRepo extends JpaRepository<User, Long> {

}