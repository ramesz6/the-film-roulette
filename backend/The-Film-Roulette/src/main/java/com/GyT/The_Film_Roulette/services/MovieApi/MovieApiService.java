package com.gyt.the_film_roulette.services.MovieApi;

import com.gyt.the_film_roulette.dtos.DiscoveryResponse;

/**
 * Service interface for fetching movie discovery results from the TMDB API.
 * Defines a method to retrieve a list of discovered movies.
 *
 * @return A {@link DiscoveryResponse} containing the movie data.
 */
public interface MovieApiService {

  public DiscoveryResponse getResult();
}
