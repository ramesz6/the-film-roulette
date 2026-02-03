package com.gyt.thefilmroulette.services.api;

import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.dtos.GenresResponse;
import com.gyt.thefilmroulette.dtos.TitleDetails;

/**
 * Service interface for fetching movie discovery results from the TMDB API.
 * Defines a method to retrieve a list of discovered movies.
 *
 * @return A {@link DiscoveryResponse} containing the movie data.
 */
public interface MovieApiService {

  public DiscoveryResponse getResult();

  public GenresResponse getGenres();

  public TitleDetails getDetails(String mediaType, int id);
}
