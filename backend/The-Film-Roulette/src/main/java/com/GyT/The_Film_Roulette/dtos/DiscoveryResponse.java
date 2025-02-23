package com.gyt.the_film_roulette.dtos;

import com.gyt.the_film_roulette.models.Movie;
import java.util.List;

/**
 * DTO (Data Transfer Object) for handling movie discovery responses.
 * Represents a paginated response containing a list of movies.
 *
 * @param page    The current page number of the response.
 * 
 * @param results The list of movies retrieved from the TMDB API.
 */

public record DiscoveryResponse(
    int page,
    List<Movie> results) {

}
