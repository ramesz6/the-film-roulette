package com.gyt.thefilmroulette.dtos;

import java.util.List;

/**
 * API response model for title details used by the frontend list views.
 * This is a normalized shape for both TMDB movie and TV data.
 */
public record TitleDetails(
    int id,
    String title,
    String name,
    String overview,
    String posterPath,
    String releaseDate,
    String firstAirDate,
    List<Integer> genreIds,
    List<String> genres,
    Double userScore,
    Integer voteCount,
    Integer runtimeMinutes,
    Integer numberOfSeasons,
    Integer numberOfEpisodes,
    String trailerUrl,
    OttOffer ottOffer) {

  /** Normalized watch-provider offers for a single region. */
  public record OttOffer(
      String region,
      String link,
      List<OttProvider> flatrate,
      List<OttProvider> rent,
      List<OttProvider> buy) {
  }

  /** Single watch provider (display name + optional logo URL). */
  public record OttProvider(String name, String logoUrl) {}
}
