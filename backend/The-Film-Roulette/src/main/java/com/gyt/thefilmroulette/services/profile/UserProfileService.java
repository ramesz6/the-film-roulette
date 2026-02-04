package com.gyt.thefilmroulette.services.profile;

import com.gyt.thefilmroulette.dtos.profile.ListEntryRequest;
import com.gyt.thefilmroulette.dtos.profile.ListEntryResponse;
import com.gyt.thefilmroulette.dtos.profile.PreferencesRequest;
import com.gyt.thefilmroulette.dtos.profile.PreferencesResponse;
import com.gyt.thefilmroulette.models.ListStatus;
import com.gyt.thefilmroulette.models.User;
import com.gyt.thefilmroulette.models.UserListEntry;
import com.gyt.thefilmroulette.models.UserPreferences;
import com.gyt.thefilmroulette.repositories.UserListEntryRepository;
import com.gyt.thefilmroulette.repositories.UserPreferencesRepository;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Manages per-user profile preferences and per-user lists (watch later / seen /
 * disliked).
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {

  private static final int DEFAULT_YEAR_FROM = 1900;

  private final UserPreferencesRepository userPreferencesRepository;
  private final UserListEntryRepository userListEntryRepository;

  /**
   * Returns the user's saved preferences, creating defaults if missing.
   *
   * @param user authenticated user
   *
   * @return preference settings
   */
  public PreferencesResponse getPreferences(User user) {
    Objects.requireNonNull(user, "user");

    UserPreferences prefs = userPreferencesRepository.findByUserId(user.getId()).orElse(null);
    if (prefs == null) {
      int currentYear = Year.now().getValue();
      UserPreferences created = UserPreferences.builder()
          .user(user)
          .likedGenreIds(new ArrayList<>())
          .yearFrom(DEFAULT_YEAR_FROM)
          .yearTo(currentYear)
          .includeMovies(true)
          .includeSeries(false)
          .build();
      prefs = userPreferencesRepository.save(Objects.requireNonNull(created, "created"));
    } else {
      boolean changed = false;
      if (prefs.getYearFrom() == null) {
        prefs.setYearFrom(DEFAULT_YEAR_FROM);
        changed = true;
      }
      if (prefs.getYearTo() == null) {
        prefs.setYearTo(Year.now().getValue());
        changed = true;
      }
      if (changed) {
        prefs = userPreferencesRepository.save(prefs);
      }
    }

    return new PreferencesResponse(
        List.copyOf(prefs.getLikedGenreIds()),
        prefs.getYearFrom(),
        prefs.getYearTo(),
        prefs.isIncludeMovies(),
        prefs.isIncludeSeries());
  }

  /**
   * Updates the user's preference settings.
   *
   * @param user    authenticated user
   * @param request new preference values
   *
   * @return updated preferences
   */
  public PreferencesResponse updatePreferences(User user, PreferencesRequest request) {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(request, "request");

    if (request.yearFrom() != null
        && request.yearTo() != null
        && request.yearFrom() > request.yearTo()) {
      throw new IllegalArgumentException("yearFrom must be <= yearTo");
    }

    UserPreferences prefs = userPreferencesRepository.findByUserId(user.getId()).orElse(null);
    if (prefs == null) {
      prefs = UserPreferences.builder().user(user).build();
    }

    prefs.setLikedGenreIds(
        request.likedGenreIds() == null
            ? new ArrayList<>()
            : new ArrayList<>(request.likedGenreIds()));
    prefs.setYearFrom(request.yearFrom());
    prefs.setYearTo(request.yearTo());
    prefs.setIncludeMovies(request.includeMovies());
    prefs.setIncludeSeries(request.includeSeries());

    UserPreferences saved = userPreferencesRepository.save(Objects.requireNonNull(prefs, "prefs"));

    return new PreferencesResponse(
        List.copyOf(saved.getLikedGenreIds()),
        saved.getYearFrom(),
        saved.getYearTo(),
        saved.isIncludeMovies(),
        saved.isIncludeSeries());
  }

  /**
   * Returns entries from one of the user's lists.
   *
   * @param user   authenticated user
   * @param status list status to query
   *
   * @return list entries
   */
  public List<ListEntryResponse> getList(User user, ListStatus status) {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(status, "status");

    return userListEntryRepository
        .findByUserIdAndStatus(user.getId(), status)
        .stream()
        .map(entry -> new ListEntryResponse(
            entry.getTmdbId(),
            entry.getMediaType(),
            entry.getStatus()))
        .toList();
  }

  /**
   * Returns normalized exclude keys for one of the user's lists.
   *
   * @param user   authenticated user
   * @param status list status to query
   *
   * @return exclude keys in the form "mediaType:tmdbId"
   */
  public Set<String> getListKeys(User user, ListStatus status) {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(status, "status");

    Set<String> result = new HashSet<>();
    for (UserListEntry entry : userListEntryRepository
        .findByUserIdAndStatus(user.getId(), status)) {
      if (entry == null) {
        continue;
      }
      String mediaType = entry.getMediaType();
      if (mediaType == null || mediaType.isBlank()) {
        continue;
      }
      result.add(mediaType.trim().toLowerCase() + ":" + entry.getTmdbId());
    }
    return result;
  }

  /**
   * Adds or updates a title entry with the given status.
   *
   * @param user    authenticated user
   * @param status  list status to set
   * @param request title details
   */
  public void setListStatus(User user, ListStatus status, ListEntryRequest request) {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(status, "status");
    Objects.requireNonNull(request, "request");

    String mediaType = normalizeMediaType(request.mediaType());

    UserListEntry entry = userListEntryRepository
        .findByUserIdAndMediaTypeAndTmdbId(user.getId(), mediaType, request.tmdbId())
        .orElseGet(() -> UserListEntry.builder()
            .user(user)
            .mediaType(mediaType)
            .tmdbId(request.tmdbId())
            .build());

    entry.setStatus(status);
    userListEntryRepository.save(entry);
  }

  /**
   * Removes a title from all of the user's lists.
   *
   * @param user      authenticated user
   * @param mediaType media type (movie or tv)
   * @param tmdbId    TMDB id
   */
  public void removeFromList(User user, String mediaType, int tmdbId) {
    Objects.requireNonNull(user, "user");
    mediaType = normalizeMediaType(mediaType);

    userListEntryRepository
        .findByUserIdAndMediaTypeAndTmdbId(user.getId(), mediaType, tmdbId)
        .ifPresent(userListEntryRepository::delete);
  }

  private static String normalizeMediaType(String mediaType) {
    String value = Objects.requireNonNull(mediaType, "mediaType").trim().toLowerCase();

    return switch (value) {
      case "movie" -> "movie";
      case "tv", "series" -> "tv";
      default -> throw new IllegalArgumentException("Unsupported mediaType: " + mediaType);
    };
  }
}
