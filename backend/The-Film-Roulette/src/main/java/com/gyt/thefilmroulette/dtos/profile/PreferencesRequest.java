package com.gyt.thefilmroulette.dtos.profile;

import java.util.List;

/**
 * Request DTO for updating user preferences.
 */
public record PreferencesRequest(
    List<Integer> likedGenreIds,
    Integer yearFrom,
    Integer yearTo,
    boolean includeMovies,
    boolean includeSeries) {
}
