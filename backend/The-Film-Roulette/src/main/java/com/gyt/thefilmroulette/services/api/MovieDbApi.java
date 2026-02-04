package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.dtos.DiscoveryTitlesResponse;
import com.gyt.thefilmroulette.dtos.GenreListResponse;
import com.gyt.thefilmroulette.dtos.TitleDetails;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Retrofit interface for interacting with the TMDB API.
 * Defines an endpoint for discovering movies.
 *
 * @return A {@link Call} object containing a {@link DiscoveryResponse}.
 */
public interface MovieDbApi {

  @GET("discover/movie")
  public Call<DiscoveryResponse> getResult();

  @GET("discover/movie")
  public Call<DiscoveryTitlesResponse> discoverMovie(@QueryMap Map<String, String> query);

  @GET("discover/tv")
  public Call<DiscoveryTitlesResponse> discoverTv(@QueryMap Map<String, String> query);

  @GET("genre/movie/list")
  public Call<GenreListResponse> getMovieGenres();

  @GET("genre/tv/list")
  public Call<GenreListResponse> getTvGenres();

  @GET("movie/{id}")
  public Call<TitleDetails> getMovieDetails(@Path("id") int id);

  @GET("tv/{id}")
  public Call<TitleDetails> getTvDetails(@Path("id") int id);

}
