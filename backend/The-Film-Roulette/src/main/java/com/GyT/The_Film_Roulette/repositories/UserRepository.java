package com.GyT.The_Film_Roulette.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GyT.The_Film_Roulette.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // boolean existsByEmail(String email);

    // void deleteByUsername(String name);

}
