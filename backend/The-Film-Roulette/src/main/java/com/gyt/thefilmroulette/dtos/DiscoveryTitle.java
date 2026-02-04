package com.gyt.thefilmroulette.dtos;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Minimal discovery result item that works for both TMDB movie and TV discover
 * endpoints.
 */
public record DiscoveryTitle(
    int id,
    @SerializedName("genre_ids") List<Integer> genreIds,
    String title,
    String name,
    String overview,
    @SerializedName("poster_path") String posterPath,
    @SerializedName("release_date") String releaseDate,
    @SerializedName("first_air_date") String firstAirDate) {
}
