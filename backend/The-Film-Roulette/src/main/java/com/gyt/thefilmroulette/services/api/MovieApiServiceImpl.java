package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.configurations.RetrofitConfig;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.dtos.DiscoveryTitlesResponse;
import com.gyt.thefilmroulette.dtos.GenreListResponse;
import com.gyt.thefilmroulette.dtos.GenresResponse;
import com.gyt.thefilmroulette.dtos.TitleDetails;
import com.gyt.thefilmroulette.exceptions.MovieApiException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

/**
 * Service implementation for fetching data from the TMDB API.
 * Uses Retrofit to communicate with the API and retrieve discovery, genre, and
 * detail data.
 */
@Service
public class MovieApiServiceImpl implements MovieApiService {

  private Retrofit retrofit;
  private MovieDbApi tmdbApi;

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
      throw new MovieApiException("Error occurred while fetching data from TMDB API");
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
      throw new MovieApiException("Error occurred while fetching genres from TMDB API");
    }
  }

  @Override
  public TitleDetails getDetails(String mediaType, int id) {
    Objects.requireNonNull(mediaType, "mediaType");

    String normalized = mediaType.trim().toLowerCase();
    try {
      var call = switch (normalized) {
        case "movie" -> tmdbApi.getMovieDetails(id);
        case "tv", "series" -> tmdbApi.getTvDetails(id);
        default -> throw new IllegalArgumentException("Unsupported mediaType: " + mediaType);
      };

      var response = call.execute();
      if (!response.isSuccessful()) {
        throw new MovieApiException("API Request failed with status code: " + response.code());
      }
      return response.body();
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new MovieApiException("Error occurred while fetching details from TMDB API");
    }
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
      throw new MovieApiException("Error occurred while fetching discovery data from TMDB API");
    }
  }
}
