package com.gyt.thefilmroulette.services.recommendation;

import com.gyt.thefilmroulette.dtos.DiscoveryTitle;
import com.gyt.thefilmroulette.dtos.DiscoveryTitlesResponse;
import com.gyt.thefilmroulette.dtos.profile.PreferencesResponse;
import com.gyt.thefilmroulette.dtos.recommendation.RecommendationResponse;
import com.gyt.thefilmroulette.models.ListStatus;
import com.gyt.thefilmroulette.models.User;
import com.gyt.thefilmroulette.services.api.MovieApiService;
import com.gyt.thefilmroulette.services.profile.UserProfileService;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Builds roulette recommendations based on a user's preferences.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

  private static final int MAX_TOTAL_PAGES = 20;
  private static final int MAX_ATTEMPTS = 10;
  private final SecureRandom random = new SecureRandom();

  private final MovieApiService movieApiService;
  private final UserProfileService userProfileService;

  /**
   * Returns the next roulette recommendation.
   *
   * <p>Client-provided excludes are merged with server-side excludes (seen and disliked).
   *
   * @param user        authenticated user
   * @param excludeKeys client-provided exclude keys
   * @return a recommended title
   */
  public RecommendationResponse next(User user, List<String> excludeKeys) {
    Objects.requireNonNull(user, "user");

    PreferencesResponse prefs = userProfileService.getPreferences(user);
    if (!isProfileConfigured(prefs)) {
      throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "Profile not configured");
    }

    Set<String> exclude = new HashSet<>(normalizeExclude(excludeKeys));
    exclude.addAll(userProfileService.getListKeys(user, ListStatus.DISLIKED));
    exclude.addAll(userProfileService.getListKeys(user, ListStatus.SEEN));
    List<String> mediaTypes = allowedMediaTypes(prefs);

    if (mediaTypes.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "Profile not configured");
    }

    Map<String, String> baseQuery = baseQueryFromPreferences(prefs);
    String from = prefs.yearFrom() + "-01-01";
    String to = prefs.yearTo() + "-12-31";

    for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
      String mediaType = mediaTypes.get(random.nextInt(mediaTypes.size()));
      Map<String, String> typedQuery = withDateRange(baseQuery, mediaType, from, to);

      int totalPages = 1;
      try {
        DiscoveryTitlesResponse first = movieApiService.discover(
            mediaType,
            withPage(typedQuery, 1));
        totalPages = Math.max(1, Math.min(MAX_TOTAL_PAGES, first.totalPages()));
      } catch (Exception ignored) {
        continue;
      }

      int page = 1 + random.nextInt(totalPages);
      DiscoveryTitlesResponse resp;
      try {
        resp = movieApiService.discover(mediaType, withPage(typedQuery, page));
      } catch (Exception ignored) {
        continue;
      }

      List<DiscoveryTitle> candidates = (resp.results() == null
          ? List.<DiscoveryTitle>of()
          : resp.results())
          .stream()
          .filter(item -> item != null)
          .filter(item -> !exclude.contains(key(mediaType, item.id())))
          .toList();

      if (candidates.isEmpty()) {
        continue;
      }

      DiscoveryTitle picked = candidates.get(random.nextInt(candidates.size()));
      String title = picked.title() != null && !picked.title().isBlank()
          ? picked.title()
          : picked.name();
      String date = picked.releaseDate() != null && !picked.releaseDate().isBlank()
          ? picked.releaseDate()
          : picked.firstAirDate();

      return new RecommendationResponse(
          mediaType,
          picked.id(),
          title,
          picked.overview(),
          picked.posterPath(),
          date,
          picked.genreIds() == null ? List.of() : picked.genreIds());
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendation found");
  }

  private static boolean isProfileConfigured(PreferencesResponse prefs) {
    if (prefs == null) {
      return false;
    }
    if (prefs.likedGenreIds() == null || prefs.likedGenreIds().isEmpty()) {
      return false;
    }
    if (prefs.yearFrom() == null || prefs.yearTo() == null) {
      return false;
    }
    return prefs.includeMovies() || prefs.includeSeries();
  }

  private static List<String> allowedMediaTypes(PreferencesResponse prefs) {
    if (prefs == null) {
      return List.of();
    }
    if (prefs.includeMovies() && prefs.includeSeries()) {
      return List.of("movie", "tv");
    }
    if (prefs.includeMovies()) {
      return List.of("movie");
    }
    if (prefs.includeSeries()) {
      return List.of("tv");
    }
    return List.of();
  }

  private static Map<String, String> baseQueryFromPreferences(PreferencesResponse prefs) {
    Map<String, String> query = new HashMap<>();
    query.put("include_adult", "false");
    query.put("sort_by", "popularity.desc");
    query.put("vote_count.gte", "50");

    String genres = prefs.likedGenreIds().stream()
        .filter(Objects::nonNull)
        .distinct()
        .map(String::valueOf)
        .collect(Collectors.joining("|"));
    if (!genres.isBlank()) {
      query.put("with_genres", genres);
    }

    return query;
  }

  private static Map<String, String> withDateRange(
      Map<String, String> base,
      String mediaType,
      String from,
      String to) {

    Map<String, String> query = new HashMap<>(base);
    String mt = Objects.requireNonNull(mediaType, "mediaType").trim().toLowerCase();

    if (mt.equals("movie")) {
      query.put("primary_release_date.gte", from);
      query.put("primary_release_date.lte", to);
    } else {
      query.put("first_air_date.gte", from);
      query.put("first_air_date.lte", to);
    }

    return query;
  }

  private static Map<String, String> withPage(Map<String, String> base, int page) {
    Map<String, String> query = new HashMap<>(base);
    query.put("page", String.valueOf(page));
    return query;
  }

  private static Set<String> normalizeExclude(List<String> excludeKeys) {
    if (excludeKeys == null || excludeKeys.isEmpty()) {
      return Set.of();
    }
    Set<String> result = new HashSet<>();
    for (String raw : excludeKeys) {
      if (raw == null) {
        continue;
      }
      String trimmed = raw.trim();
      if (trimmed.isBlank()) {
        continue;
      }
      result.add(trimmed);
    }
    return result;
  }

  private static String key(String mediaType, int tmdbId) {
    String mt = Objects.requireNonNull(mediaType, "mediaType").trim().toLowerCase();
    return mt + ":" + tmdbId;
  }
}
