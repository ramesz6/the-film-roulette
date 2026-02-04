package com.gyt.thefilmroulette.dtos;

import java.util.List;

/**
 * DTO representing a TMDB genre list response.
 */
public record GenreListResponse(
    List<GenreDto> genres) {
}
