package com.gyt.thefilmroulette.dtos.register;

/**
 * DTO (Data Transfer Object) for registration requests.
 * Contains the user information required for the registration process,
 * such as username, email, and password.
 */
public record RegisterRequest(
                String username,
                String email,
                String password) {
}
