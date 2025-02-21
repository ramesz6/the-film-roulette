package com.GyT.The_Film_Roulette.dtos;

import com.GyT.The_Film_Roulette.models.Movie;
import java.util.List;

public record DiscoveryResponse(
    int page,
    List<Movie> results) {

}
