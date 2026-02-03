package com.gyt.thefilmroulette.dtos.register;

import org.springframework.lang.NonNull;

/**
 * DTO (Data Transfer Object) for registration requests.
 * Contains the user information required for the registration process,
 * such as username, email, and password.
 */
public record RegisterRequest(
        @NonNull String username,
        @NonNull String email,
        @NonNull String password) {
}
