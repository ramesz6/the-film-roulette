/**
 * Repository for accessing user preference entities.
 */
package com.gyt.thefilmroulette.repositories;

import com.gyt.thefilmroulette.models.UserPreferences;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

  Optional<UserPreferences> findByUserId(Long userId);
}
