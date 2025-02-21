package com.GyT.The_Film_Roulette.services.TMDB;

import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TMDBApi {

  @GET("discover/movie")
  public Call<DiscoveryResponse> getResult();

}
