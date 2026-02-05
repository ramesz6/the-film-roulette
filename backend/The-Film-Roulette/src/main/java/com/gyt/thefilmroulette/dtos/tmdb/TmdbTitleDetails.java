package com.gyt.thefilmroulette.dtos.tmdb;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Partial TMDB details DTO for both /movie/{id} and /tv/{id} endpoints.
 * Includes optional embedded responses via append_to_response (videos, watch/providers).
 * Gson ignores unknown fields.
 */
public record TmdbTitleDetails(
    int id,
    String title,
    String name,
    String overview,
    @SerializedName("poster_path") String posterPath,
    @SerializedName("release_date") String releaseDate,
    @SerializedName("first_air_date") String firstAirDate,
    @SerializedName("vote_average") Double voteAverage,
    @SerializedName("vote_count") Integer voteCount,
    // movie-only
    Integer runtime,
    // tv-only
    @SerializedName("number_of_seasons") Integer numberOfSeasons,
    @SerializedName("number_of_episodes") Integer numberOfEpisodes,
    @SerializedName("genres") List<TmdbGenre> genres,
    @SerializedName("videos") TmdbVideos videos,
    @SerializedName("watch/providers") TmdbWatchProviders watchProviders) {

  /** TMDB genre item. */
  public record TmdbGenre(int id, String name) {}

  /** Embedded videos response. */
  public record TmdbVideos(@SerializedName("results") List<TmdbVideo> results) {}

  /** TMDB video (trailer/teaser/etc). */
  public record TmdbVideo(
      String id,
      String key,
      String name,
      String site,
      String type,
      Boolean official) {}

  /** Embedded watch providers response, keyed by region (e.g. HU, US). */
  public record TmdbWatchProviders(
      @SerializedName("results") Map<String, TmdbWatchRegion> results) {}

  /** Watch providers for a single region. */
  public record TmdbWatchRegion(
      String link,
      List<TmdbProvider> flatrate,
      List<TmdbProvider> rent,
      List<TmdbProvider> buy) {}

  /** Single provider entry (name + logo path). */
  public record TmdbProvider(
      @SerializedName("provider_name") String providerName,
      @SerializedName("logo_path") String logoPath) {}
}
