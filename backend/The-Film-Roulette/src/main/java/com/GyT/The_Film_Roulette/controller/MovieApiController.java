package com.gyt.the_film_roulette.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gyt.the_film_roulette.dtos.DiscoveryResponse;
import com.gyt.the_film_roulette.services.MovieApi.MovieApiService;

/**
 * REST controller for handling TMDB movie-related requests.
 * Provides an endpoint to discover movies using the TMDB service.
 */
@RequestMapping("api/v1/movie")
@RestController
@RequiredArgsConstructor
public class MovieApiController {
  private MovieApiService tmdbService;

  @GetMapping("/discover")
  public DiscoveryResponse discover() {

    return tmdbService.getResult();
  }

}