package com.gyt.thefilmroulette.dtos.profile;

import org.springframework.lang.NonNull;

/**
 * Request DTO for adding a title to a user list.
 */
public record ListEntryRequest(
    int tmdbId,
    @NonNull String mediaType) {
}
