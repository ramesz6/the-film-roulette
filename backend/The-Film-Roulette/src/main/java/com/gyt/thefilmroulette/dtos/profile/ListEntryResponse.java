package com.gyt.thefilmroulette.dtos.profile;

import com.gyt.thefilmroulette.models.ListStatus;

/**
 * Response DTO for a user list entry.
 */
public record ListEntryResponse(
    int tmdbId,
    String mediaType,
    ListStatus status) {
}
