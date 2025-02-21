package com.GyT.The_Film_Roulette.services.TMDB;

import com.GyT.The_Film_Roulette.dtos.ApiToken;
import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;

public interface TMDBService {

  public ApiToken getToken();

  public DiscoveryResponse getResult();
}
