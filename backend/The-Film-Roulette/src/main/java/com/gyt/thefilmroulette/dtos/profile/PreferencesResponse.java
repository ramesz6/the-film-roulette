package com.gyt.thefilmroulette.dtos.profile;

import java.util.List;

public record PreferencesResponse(
    List<Integer> likedGenreIds,
    Integer yearFrom,
    Integer yearTo,
    boolean includeMovies,
    boolean includeSeries) {
}
