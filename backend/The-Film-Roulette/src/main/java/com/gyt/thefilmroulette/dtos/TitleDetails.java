package com.gyt.thefilmroulette.dtos;

import java.util.List;

/**
 * Minimal title details DTO for both TMDB movie and TV endpoints.
 * Gson will ignore unknown fields.
 */
public record TitleDetails(
    int id,
    String title,
    String name,
    String overview,
    String posterPath,
    String releaseDate,
    String firstAirDate,
    List<Integer> genreIds) {
}
