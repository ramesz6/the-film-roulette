package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.configurations.RetrofitConfig;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.exceptions.MovieApiException;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

/**
 * Service implementation for fetching movie discovery results from the TMDB
 * API.
 * Uses Retrofit to communicate with the API and retrieve movie data.
 *
 * <p>This service class handles the interaction with the TMDB API to retrieve
 * movie discovery results.
 * It initializes the Retrofit client and calls the TMDB API endpoint to get the
 * movie data.
 * If the API call is unsuccessful, a {@link MovieApiException} is thrown.
 */
@Service
public class MovieApiServiceImpl implements MovieApiService {

  private Retrofit retrofit;
  private MovieDbApi tmdbApi;

  /**
   * Constructs a new MovieApiServiceImpl with the provided RetrofitConfig.
   * Initializes the Retrofit client and TMDB API interface.
   *
   * @param retrofitConfig the configuration object containing the Retrofit client
   *                       setup
   *
   * 
   * @throws IllegalArgumentException if the retrofitConfig or Retrofit object is
   *                                  null
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
   * <p>Executes the TMDB API call and returns the {@link DiscoveryResponse} if
   * successful.
   *
   * @return the discovery results as a {@link DiscoveryResponse}
   * 
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
}
