package com.gyt.thefilmroulette.dtos;

import java.util.List;

public record GenreListResponse(
    List<GenreDto> genres) {
}
