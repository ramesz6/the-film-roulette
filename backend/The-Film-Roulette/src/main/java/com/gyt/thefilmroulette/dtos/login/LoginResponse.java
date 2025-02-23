package com.gyt.thefilmroulette.dtos.login;

/**
 * DTO (Data Transfer Object) for login responses.
 * Contains the JWT token returned after successful authentication.
 */
public record LoginResponse(
    String token) {
}
