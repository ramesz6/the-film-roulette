package com.GyT.The_Film_Roulette.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.GyT.The_Film_Roulette.models.User;;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<UserDetails> findByUsername(String username);

}