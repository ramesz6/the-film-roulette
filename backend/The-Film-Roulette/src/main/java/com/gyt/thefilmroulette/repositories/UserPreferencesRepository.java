package com.gyt.thefilmroulette.repositories;

import com.gyt.thefilmroulette.models.UserPreferences;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing user preference entities.
 */
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

  Optional<UserPreferences> findByUserId(Long userId);
}
