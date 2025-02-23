package com.gyt.thefilmroulette.services.MovieApi;

import com.gyt.thefilmroulette.dtos.DiscoveryResponse;

/**
 * Service interface for fetching movie discovery results from the TMDB API.
 * Defines a method to retrieve a list of discovered movies.
 *
 * @return A {@link DiscoveryResponse} containing the movie data.
 */
public interface MovieApiService {

  public DiscoveryResponse getResult();
}
