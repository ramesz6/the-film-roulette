package com.gyt.thefilmroulette.dtos.login;

import org.springframework.lang.NonNull;

/**
 * DTO (Data Transfer Object) for Google login requests.
 * Contains the Google Identity Services ID token (credential).
 */
public record GoogleLoginRequest(
    @NonNull String credential) {
}
