package com.GyT.The_Film_Roulette.dtos.login;

/**
 * DTO (Data Transfer Object) for login requests.
 * Contains the email and password required for authentication.
 */
public record LoginRequest(
    String email,
    String password) {
}
