package com.gyt.thefilmroulette.dtos.profile;

import com.gyt.thefilmroulette.models.ListStatus;

public record ListEntryResponse(
    int tmdbId,
    String mediaType,
    ListStatus status) {
}
