package com.GyT.The_Film_Roulette.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GyT.The_Film_Roulette.models.User;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * Extends {@link JpaRepository} to leverage Spring Data JPA's capabilities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by their email.
   * 
   * @param email the email of the user
   * @return an {@link Optional} containing the user if found, otherwise empty
   */
  Optional<User> findByEmail(String email);

  /**
   * Finds a user by their username.
   * 
   * @param username the username of the user
   *
   * @return an {@link Optional} containing the user if found, otherwise empty
   */
  Optional<User> findByUsername(String username);

  /**
   * Checks if a user with the specified email exists.
   * 
   * @param email the email of the user
   *
   * @return true if a user with the given email exists, otherwise false
   */
  boolean existsByEmail(String email);
}
