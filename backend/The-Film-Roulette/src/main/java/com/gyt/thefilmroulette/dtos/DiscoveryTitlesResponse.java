package com.gyt.thefilmroulette.dtos;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Paginated TMDB discover response for both movie and TV.
 */
public record DiscoveryTitlesResponse(
    int page,
    @SerializedName("total_pages") int totalPages,
    List<DiscoveryTitle> results) {
}
