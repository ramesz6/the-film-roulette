package com.gyt.thefilmroulette.dtos.recommendation;

import java.util.List;

public record RecommendationResponse(
    String mediaType,
    int tmdbId,
    String title,
    String overview,
    String posterPath,
    String releaseDate,
    List<Integer> genreIds) {
}
