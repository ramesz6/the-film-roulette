package com.gyt.thefilmroulette.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.login.LoginRequest;
import com.gyt.thefilmroulette.dtos.register.RegisterRequest;
import com.gyt.thefilmroulette.exceptions.AuthenticationException;
import jakarta.transaction.Transactional;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the AuthenticationController.
 * Contains tests for the authentication endpoints, including successful and
 * unsuccessful registration scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerTest {

    /**
     * ObjectMapper instance used for converting Java objects to JSON and vice
     * versa.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** MockMvc instance used for performing HTTP requests in tests. */
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should successfully register a new user")
    public void authControllerShouldSuccessfullyRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "ramesz",
                "ramesz@email.com",
                "password");

        String stringified = Objects.requireNonNull(
                objectMapper.writeValueAsString(registerRequest));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringified))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should not allow duplicate user registration")
    public void authControllerShouldNotRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "ramesz",
                "ramesz@email.com",
                "password");

        String stringified = Objects.requireNonNull(
                objectMapper.writeValueAsString(registerRequest));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringified))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringified))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully login and return JWT token")
    public void authControllerShouldSuccessfullyLoginAndReturnWithToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "ramesz",
                "ramesz@email.com",
                "password");

        String stringifiedRegister = Objects.requireNonNull(
                objectMapper.writeValueAsString(registerRequest));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringifiedRegister))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest(
                "ramesz@email.com",
                "password");

        String stringifiedLogin = Objects.requireNonNull(
                objectMapper.writeValueAsString(loginRequest));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringifiedLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    public void authControllerShouldFailLoginWithInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "nonexistent@email.com",
                "wrongpassword");

        String stringifiedLogin = Objects.requireNonNull(
                objectMapper.writeValueAsString(loginRequest));

        try {
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(stringifiedLogin))
                    .andExpect(status().isBadRequest());
        } catch (Exception ex) {
            Throwable root = rootCause(ex);
            assertEquals(AuthenticationException.class, root.getClass());
            assertEquals("Invalid email or password", root.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    public void authControllerShouldFailLoginWithWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "test@email.com",
                "correctpassword");

        String stringifiedRegister = Objects.requireNonNull(
                objectMapper.writeValueAsString(registerRequest));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(stringifiedRegister))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest(
                "test@email.com",
                "wrongpassword");

        String stringifiedLogin = Objects.requireNonNull(
                objectMapper.writeValueAsString(loginRequest));

        try {
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(stringifiedLogin))
                    .andExpect(status().isBadRequest());
        } catch (Exception ex) {
            Throwable root = rootCause(ex);
            assertEquals(AuthenticationException.class, root.getClass());
            assertEquals("Invalid email or password", root.getMessage());
        }
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }

    @Test
    @DisplayName("Should handle malformed JSON in registration")
    public void authControllerShouldHandleMalformedJson() throws Exception {
        String malformedJson = "{ \"username\": \"test\", \"email\": }";

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing content type")
    public void authControllerShouldHandleMissingContentType() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "test@email.com",
                "password");

        String stringified = Objects.requireNonNull(
                objectMapper.writeValueAsString(registerRequest));

        mockMvc.perform(post("/api/v1/auth/register")
                .content(stringified))
                .andExpect(status().isUnsupportedMediaType());
    }
}
