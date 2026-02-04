package com.gyt.thefilmroulette.dtos.profile;

import java.util.List;

/**
 * Response DTO for user preferences.
 */
public record PreferencesResponse(
    List<Integer> likedGenreIds,
    Integer yearFrom,
    Integer yearTo,
    boolean includeMovies,
    boolean includeSeries) {
}
