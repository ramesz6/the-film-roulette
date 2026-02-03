package com.gyt.thefilmroulette.repositories;

import com.gyt.thefilmroulette.models.ListStatus;
import com.gyt.thefilmroulette.models.UserListEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserListEntryRepository extends JpaRepository<UserListEntry, Long> {

  List<UserListEntry> findByUserIdAndStatus(Long userId, ListStatus status);

  Optional<UserListEntry> findByUserIdAndMediaTypeAndTmdbId(Long userId, String mediaType, int tmdbId);
}
