package com.gyt.thefilmroulette.services;

import java.util.List;

import com.gyt.thefilmroulette.models.User;

/**
 * Interface for user management services.
 * <p>
 * Provides methods to add, update, delete, and retrieve users from the system.
 * </p>
 */
public interface UserService {

  /**
   * Adds a new user to the system.
   * <p>
   * This method persists the user in the database and returns the saved user.
   * </p>
   * 
   * @param user The user to be added.
   * @return The added user with a generated ID.
   */
  User addUsers(User user);

  /**
   * Retrieves a list of all users in the system.
   * 
   * @return A list of all users.
   */
  List<User> getUsers();

  /**
   * Updates an existing user’s information.
   * <p>
   * This method updates the user details based on the provided ID. If the user
   * exists, it updates the record.
   * </p>
   * 
   * @param user The updated user information.
   * @param id   The ID of the user to be updated.
   * @return The updated user.
   */
  User updateUser(User user, Long id);

  /**
   * Retrieves a user by their ID.
   * 
   * @param id The ID of the user to retrieve.
   * @return The user with the given ID.
   */
  User getUserById(Long id);

  /**
   * Deletes a user by their ID.
   * 
   * @param id The ID of the user to be deleted.
   */
  void deleteUser(Long id);

}
