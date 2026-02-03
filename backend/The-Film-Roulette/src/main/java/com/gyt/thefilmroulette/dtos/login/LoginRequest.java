package com.gyt.thefilmroulette.dtos.login;

import org.springframework.lang.NonNull;

/**
 * DTO (Data Transfer Object) for login requests.
 * Contains the email and password required for authentication.
 */
public record LoginRequest(
        @NonNull String email,
        @NonNull String password) {
}
