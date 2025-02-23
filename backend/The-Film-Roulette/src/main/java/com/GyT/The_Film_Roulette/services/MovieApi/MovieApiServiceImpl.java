package com.gyt.the_film_roulette.services.MovieApi;

import org.springframework.stereotype.Service;

import com.gyt.the_film_roulette.configurations.RetrofitConfig;
import com.gyt.the_film_roulette.dtos.DiscoveryResponse;

import retrofit2.Retrofit;

/**
 * Service implementation for fetching movie discovery results from the TMDB
 * API.
 * Uses Retrofit to communicate with the API and retrieve movie data.
 */
@Service
public class MovieApiServiceImpl implements MovieApiService {

  private Retrofit retrofit;
  private MovieDbApi tmdbApi;

  public MovieApiServiceImpl(RetrofitConfig retrofitConfig) {
    this.retrofit = retrofitConfig.retrofit();
    this.tmdbApi = retrofit.create(MovieDbApi.class);
  }

  @Override
  public DiscoveryResponse getResult() {
    try {
      var thing = tmdbApi.getResult();
      System.out.println(thing);
      var otherThing = thing.execute();
      System.out.println(otherThing.isSuccessful());
      var theThing = otherThing.body();
      System.out.println(theThing);
      return theThing;
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }

}