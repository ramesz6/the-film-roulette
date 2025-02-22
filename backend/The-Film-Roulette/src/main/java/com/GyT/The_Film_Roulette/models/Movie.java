package com.GyT.The_Film_Roulette.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a movie retrieved from the TMDB API.
 * Contains metadata about the movie, including its title, overview, popularity,
 * and more.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

  private boolean adult;
  private String backdropPath;
  private List<Integer> genreIds;
  private int id;
  private String originalLanguage;
  private String originalTitle;
  private String overview;
  private double popularity;
  private String posterPath;
  private String releaseDate;
  private String title;
  private boolean video;
  private double voteAverage;
  private int voteCount;

}
