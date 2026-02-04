/**
 * Request DTO for adding a title to a user list.
 */
package com.gyt.thefilmroulette.dtos.profile;

import org.springframework.lang.NonNull;

public record ListEntryRequest(
    int tmdbId,
    @NonNull String mediaType) {
}
