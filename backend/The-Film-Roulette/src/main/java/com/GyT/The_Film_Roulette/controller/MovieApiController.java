package com.GyT.The_Film_Roulette.controller;

import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;
import com.GyT.The_Film_Roulette.services.MovieApi.MovieApiService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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