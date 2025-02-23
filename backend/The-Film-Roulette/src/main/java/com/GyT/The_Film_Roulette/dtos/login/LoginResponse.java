package com.gyt.the_film_roulette.dtos.login;

/**
 * DTO (Data Transfer Object) for login responses.
 * Contains the JWT token returned after successful authentication.
 */
public record LoginResponse(
                String token) {
}
