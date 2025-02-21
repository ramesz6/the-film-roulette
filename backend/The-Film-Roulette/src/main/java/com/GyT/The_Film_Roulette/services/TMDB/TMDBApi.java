package com.GyT.The_Film_Roulette.services.TMDB;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.GyT.The_Film_Roulette.dtos.ApiToken;
import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TMDBApi {

  @GET("authentication/token/new")
  public Call<ApiToken> getToken();

  @GET("discover/movie")
  public Call<DiscoveryResponse> getResult();

}
