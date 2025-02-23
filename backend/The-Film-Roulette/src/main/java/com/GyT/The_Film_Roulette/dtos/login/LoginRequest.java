package com.gyt.the_film_roulette.dtos.login;

/**
 * DTO (Data Transfer Object) for login requests.
 * Contains the email and password required for authentication.
 */
public record LoginRequest(
                String email,
                String password) {
}
