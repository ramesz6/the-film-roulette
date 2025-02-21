package com.GyT.The_Film_Roulette.controller;

import com.GyT.The_Film_Roulette.services.TMDB.TMDBService;
import com.GyT.The_Film_Roulette.dtos.DiscoveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/movie")
@RestController
@RequiredArgsConstructor
public class TMDBApiController {
  private final TMDBService tmdbService;

  @GetMapping("/discover")
  public DiscoveryResponse discover() {

    return tmdbService.getResult();

  }

}
