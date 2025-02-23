package com.gyt.thefilmroulette.dtos;

import java.util.List;

import com.gyt.thefilmroulette.models.Movie;

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
