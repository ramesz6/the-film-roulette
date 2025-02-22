package com.GyT.The_Film_Roulette.services.TMDB;

import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit interface for interacting with the TMDB API.
 * Defines an endpoint for discovering movies.
 *
 * @return A {@link Call} object containing a {@link DiscoveryResponse}.
 */
public interface TMDBApi {

  @GET("discover/movie")
  Call<DiscoveryResponse> getResult();

}
