package com.GyT.The_Film_Roulette.services.Tmdb;

import com.GyT.The_Film_Roulette.configurations.RetrofitConfig;
import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

/**
 * Service implementation for fetching movie discovery results from the TMDB
 * API.
 * Uses Retrofit to communicate with the API and retrieve movie data.
 */
@Service
public class TmdbServiceImpl implements TmdbService {

  private Retrofit retrofit;
  private TmdbApi tmdbApi;

  public TmdbServiceImpl(RetrofitConfig retrofitConfig) {
    this.retrofit = retrofitConfig.retrofit();
    this.tmdbApi = retrofit.create(TmdbApi.class);
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