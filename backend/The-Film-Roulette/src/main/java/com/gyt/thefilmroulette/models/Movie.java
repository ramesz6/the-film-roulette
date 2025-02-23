package com.gyt.thefilmroulette.models;

import java.util.List;

/**
 * Model class representing a movie retrieved from the TMDB API.
 * Contains metadata about the movie, including its title, overview, popularity,
 * and more.
 */
public record Movie(
    boolean adult,
    String backdropPath,
    List<Integer> genreIds,
    int id,
    String originalLanguage,
    String originalTitle,
    String overview,
    double popularity,
    String posterPath,
    String releaseDate,
    String title,
    boolean video,
    double voteAverage,
    int voteCount) {
}
