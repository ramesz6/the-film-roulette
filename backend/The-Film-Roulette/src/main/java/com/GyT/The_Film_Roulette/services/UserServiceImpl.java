package com.gyt.the_film_roulette.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gyt.the_film_roulette.exceptions.EmailAlreadyTakenException;
import com.gyt.the_film_roulette.exceptions.UserNotFoundException;
import com.gyt.the_film_roulette.models.User;
import com.gyt.the_film_roulette.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the UserService interface.
 * <p>
 * This service handles the user-related operations such as adding, retrieving,
 * updating, and deleting users from the database.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  /**
   * Adds a new user to the system.
   * <p>
   * This method checks if a user with the same email already exists in the
   * system. If the email is taken, it throws an exception.
   * </p>
   * 
   * @param user The user to be added.
   * @return The added user, saved in the repository.
   * @throws EmailAlreadyTakenException if the email is already taken.
   */
  @Override
  public User addUsers(User user) {
    if (userAlreadyExists(user.getEmail())) {
      throw new EmailAlreadyTakenException(user.getEmail() + " is already taken");
    }
    return userRepository.save(user);
  }

  /**
   * Checks if a user already exists by their email.
   * 
   * @param email The email to check for existing users.
   * @return {@code true} if the email is already taken, otherwise {@code false}.
   */
  private boolean userAlreadyExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  /**
   * Retrieves a list of all users in the system.
   * 
   * @return A list of all users.
   */
  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }

  /**
   * Updates an existing user in the system.
   * <p>
   * If the user with the provided ID exists, it updates their email and username
   * and saves the changes. If the user is not found, it throws an exception.
   * </p>
   * 
   * @param user The updated user details.
   * @param id   The ID of the user to be updated.
   * @return The updated user.
   * @throws UserNotFoundException if the user with the given ID does not exist.
   */
  @Override
  public User updateUser(User user, Long id) {
    return userRepository.findById(id).map(us -> {
      us.setEmail(user.getEmail());
      us.setUsername(user.getUsername());
      return userRepository.save(us);
    }).orElseThrow(() -> new UserNotFoundException("Cannot find user"));
  }

  /**
   * Retrieves a user by their ID.
   * 
   * @param id The ID of the user to retrieve.
   * @return The user with the given ID.
   * @throws UserNotFoundException if the user with the given ID does not exist.
   */
  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Cannot find user with the Id :" + id));
  }

  /**
   * Deletes a user by their ID.
   * <p>
   * If the user with the given ID does not exist, it throws an exception.
   * </p>
   * 
   * @param id The ID of the user to be deleted.
   * @throws UserNotFoundException if the user with the given ID does not exist.
   */
  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("Cannot find user with the Id :" + id);
    }
    userRepository.deleteById(id);
  }
}
