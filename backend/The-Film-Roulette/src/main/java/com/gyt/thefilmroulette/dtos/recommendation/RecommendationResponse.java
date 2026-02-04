package com.gyt.thefilmroulette.dtos.recommendation;

import java.util.List;

/**
 * Response DTO representing a single roulette recommendation.
 */
public record RecommendationResponse(
    String mediaType,
    int tmdbId,
    String title,
    String overview,
    String posterPath,
    String releaseDate,
    List<Integer> genreIds) {
}
