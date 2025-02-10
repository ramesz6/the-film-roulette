package com.GyT.The_Film_Roulette.dtos.login;

/**
 * DTO (Data Transfer Object) for login responses.
 * Contains the JWT token returned after successful authentication.
 */
public record LoginResponse(
    String token) {
}
