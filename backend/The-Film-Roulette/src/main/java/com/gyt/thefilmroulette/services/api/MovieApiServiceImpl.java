package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.configurations.RetrofitConfig;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.dtos.DiscoveryTitlesResponse;
import com.gyt.thefilmroulette.dtos.GenreListResponse;
import com.gyt.thefilmroulette.dtos.GenresResponse;
import com.gyt.thefilmroulette.dtos.TitleDetails;
import com.gyt.thefilmroulette.dtos.tmdb.TmdbTitleDetails;
import com.gyt.thefilmroulette.exceptions.MovieApiException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

/**
 * Service implementation for fetching data from the TMDB API.
 * Uses Retrofit to communicate with the API and retrieve discovery, genre, and
 * detail data.
 */
@Service
public class MovieApiServiceImpl implements MovieApiService {

  private static final String APPEND_DETAILS = "videos,watch/providers";
  private static final String TMDB_IMAGE_BASE_W45 = "https://image.tmdb.org/t/p/w45";
  private static final String TMDB_WEB_BASE = "https://www.themoviedb.org";

  private Retrofit retrofit;
  private MovieDbApi tmdbApi;

  @Value("${tmdb.watch.region:HU}")
  private String watchRegion;

  /**
   * Constructs a new instance backed by the provided Retrofit configuration.
   *
   * @param retrofitConfig configuration object containing the Retrofit client
   *
   * @throws IllegalArgumentException if the config or Retrofit object is null
   */
  public MovieApiServiceImpl(RetrofitConfig retrofitConfig) {
    if (retrofitConfig != null) {
      this.retrofit = retrofitConfig.retrofit();
      if (this.retrofit != null) {
        this.tmdbApi = retrofit.create(MovieDbApi.class);
      } else {
        throw new IllegalArgumentException("Retrofit object is null");
      }
    } else {
      throw new IllegalArgumentException("RetrofitConfig is null");
    }
  }

  /**
   * Retrieves movie discovery results from the TMDB API.
    *
   * @return the discovery results as a {@link DiscoveryResponse}
   *
   * @throws MovieApiException if the API request fails or if any error occurs
   *                           during the request
   */
  @Override
  public DiscoveryResponse getResult() {
    try {
      var response = tmdbApi.getResult().execute();

      if (!response.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + response.code());
      }

      return response.body();
    } catch (Exception e) {
      throw new MovieApiException("Error occurred while fetching data from TMDB API", e);
    }
  }

  @Override
  public GenresResponse getGenres() {
    try {
      var movieResponse = tmdbApi.getMovieGenres().execute();
      if (!movieResponse.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + movieResponse.code());
      }

      var tvResponse = tmdbApi.getTvGenres().execute();
      if (!tvResponse.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + tvResponse.code());
      }

      GenreListResponse movieGenres = Objects.requireNonNull(movieResponse.body(), "movieGenres");
      GenreListResponse tvGenres = Objects.requireNonNull(tvResponse.body(), "tvGenres");

      return new GenresResponse(
          movieGenres.genres() == null ? List.of() : movieGenres.genres(),
          tvGenres.genres() == null ? List.of() : tvGenres.genres());
    } catch (Exception e) {
      throw new MovieApiException("Error occurred while fetching genres from TMDB API", e);
    }
  }

  @Override
  public TitleDetails getDetails(String mediaType, int id) {
    Objects.requireNonNull(mediaType, "mediaType");

    String normalized = mediaType.trim().toLowerCase();
    try {
      var call = switch (normalized) {
        case "movie" -> tmdbApi.getMovieDetails(id, APPEND_DETAILS);
        case "tv", "series" -> tmdbApi.getTvDetails(id, APPEND_DETAILS);
        default -> throw new IllegalArgumentException("Unsupported mediaType: " + mediaType);
      };

      var response = call.execute();
      if (!response.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + response.code());
      }
      TmdbTitleDetails body = response.body();
      if (body == null) {
        throw new MovieApiException("API Request returned empty body");
      }

      List<TmdbTitleDetails.TmdbGenre> rawGenres =
          body.genres() == null ? List.of() : body.genres();
      List<Integer> genreIds = rawGenres.stream().map(TmdbTitleDetails.TmdbGenre::id).toList();
      List<String> genres = rawGenres.stream().map(TmdbTitleDetails.TmdbGenre::name).toList();

      String trailerUrl = pickTrailerUrl(normalized, body.id(), body.videos());
      TitleDetails.OttOffer ottOffer = extractOttOffer(body.watchProviders());

      return new TitleDetails(
          body.id(),
          body.title(),
          body.name(),
          body.overview(),
          body.posterPath(),
          body.releaseDate(),
          body.firstAirDate(),
          genreIds,
          genres,
          body.voteAverage(),
          body.voteCount(),
          body.runtime(),
          body.numberOfSeasons(),
          body.numberOfEpisodes(),
          trailerUrl,
          ottOffer);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new MovieApiException("Error occurred while fetching details from TMDB API", e);
    }
  }

  private String pickTrailerUrl(
      String normalizedMediaType,
      int id,
      TmdbTitleDetails.TmdbVideos videos) {
    if (videos == null || videos.results() == null) {
      return null;
    }

    Comparator<TmdbTitleDetails.TmdbVideo> officialFirst =
        Comparator.comparing(
                (TmdbTitleDetails.TmdbVideo v) -> Boolean.TRUE.equals(v.official()))
            .reversed();

    Optional<TmdbTitleDetails.TmdbVideo> candidate = videos.results().stream()
        .filter(Objects::nonNull)
        .filter(v -> v.key() != null && !v.key().isBlank())
        .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
        .filter(v -> "Trailer".equalsIgnoreCase(v.type()))
        .sorted(officialFirst)
        .findFirst();

    String webType = "tv".equalsIgnoreCase(normalizedMediaType) ? "tv" : "movie";
    return candidate
        .map(v -> TMDB_WEB_BASE + "/" + webType + "/" + id + "#play=" + v.key())
        .orElse(null);
  }

  private TitleDetails.OttOffer extractOttOffer(
      TmdbTitleDetails.TmdbWatchProviders watchProviders) {
    if (watchProviders == null
        || watchProviders.results() == null
        || watchProviders.results().isEmpty()) {
      return null;
    }

    Map<String, TmdbTitleDetails.TmdbWatchRegion> results = watchProviders.results();
    String preferred = watchRegion == null ? "" : watchRegion.trim().toUpperCase();

    TmdbTitleDetails.TmdbWatchRegion region = null;
    String regionKey = null;

    if (!preferred.isBlank() && results.containsKey(preferred)) {
      regionKey = preferred;
      region = results.get(preferred);
    } else if (results.containsKey("US")) {
      regionKey = "US";
      region = results.get("US");
    } else {
      var first = results.entrySet().stream().findFirst();
      if (first.isPresent()) {
        regionKey = first.get().getKey();
        region = first.get().getValue();
      }
    }

    if (region == null) {
      return null;
    }

    return new TitleDetails.OttOffer(
        regionKey,
        region.link(),
        mapProviders(region.flatrate()),
        mapProviders(region.rent()),
        mapProviders(region.buy()));
  }

  private List<TitleDetails.OttProvider> mapProviders(
      List<TmdbTitleDetails.TmdbProvider> providers) {
    if (providers == null || providers.isEmpty()) {
      return List.of();
    }
    return providers.stream()
      .filter(Objects::nonNull)
      .map(
        p -> new TitleDetails.OttProvider(
          p.providerName(),
          p.logoPath() == null ? null : TMDB_IMAGE_BASE_W45 + p.logoPath()))
        .toList();
  }

  @Override
  public DiscoveryTitlesResponse discover(String mediaType, Map<String, String> query) {
    Objects.requireNonNull(mediaType, "mediaType");
    Objects.requireNonNull(query, "query");

    String normalized = mediaType.trim().toLowerCase();
    try {
      var call = switch (normalized) {
        case "movie" -> tmdbApi.discoverMovie(query);
        case "tv", "series" -> tmdbApi.discoverTv(query);
        default -> throw new IllegalArgumentException("Unsupported mediaType: " + mediaType);
      };

      var response = call.execute();
      if (!response.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + response.code());
      }

      DiscoveryTitlesResponse body = response.body();
      if (body == null) {
        throw new MovieApiException("API Request returned empty body");
      }
      return body;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new MovieApiException("Error occurred while fetching discovery data from TMDB API", e);
    }
  }
}
