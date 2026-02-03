package com.gyt.thefilmroulette.dtos;

import java.util.List;

public record GenresResponse(
    List<GenreDto> movie,
    List<GenreDto> tv) {
}
