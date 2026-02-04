package com.gyt.thefilmroulette.dtos;

import java.util.List;

/**
 * DTO that groups movie and TV genres.
 */
public record GenresResponse(
    List<GenreDto> movie,
    List<GenreDto> tv) {
}
