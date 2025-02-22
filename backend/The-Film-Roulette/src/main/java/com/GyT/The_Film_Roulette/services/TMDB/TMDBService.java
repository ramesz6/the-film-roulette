package com.GyT.The_Film_Roulette.services.Tmdb;

import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;

/**
 * Service interface for fetching movie discovery results from the TMDB API.
 * Defines a method to retrieve a list of discovered movies.
 *
 * @return A {@link DiscoveryResponse} containing the movie data.
 */
public interface TMDBService {

  public DiscoveryResponse getResult();
}
