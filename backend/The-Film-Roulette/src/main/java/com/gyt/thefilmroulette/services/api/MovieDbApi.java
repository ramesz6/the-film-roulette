package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit interface for interacting with the TMDB API.
 * Defines an endpoint for discovering movies.
 *
 * @return A {@link Call} object containing a {@link DiscoveryResponse}.
 */
public interface MovieDbApi {

  @GET("discover/movie")
  public Call<DiscoveryResponse> getResult();

}
