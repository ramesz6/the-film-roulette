package com.gyt.thefilmroulette.dtos;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Minimal title details DTO for both TMDB movie and TV endpoints.
 * Gson will ignore unknown fields.
 */
public record TitleDetails(
    int id,
    String title,
    String name,
    String overview,
    @SerializedName("poster_path") String posterPath,
    @SerializedName("release_date") String releaseDate,
    @SerializedName("first_air_date") String firstAirDate,
    @SerializedName("genre_ids") List<Integer> genreIds) {
}
