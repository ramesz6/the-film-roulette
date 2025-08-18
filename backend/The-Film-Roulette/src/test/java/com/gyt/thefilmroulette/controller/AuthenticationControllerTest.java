package com.gyt.thefilmroulette.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.login.LoginRequest;
import com.gyt.thefilmroulette.dtos.register.RegisterRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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

  /**
   * MockMvc instance used for performing HTTP requests in tests.
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   * Tests that a user can successfully register through the /api/v1/auth/register
   * endpoint.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should successfully register a new user")
  public void authControllerShouldSuccessfullyRegister() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "ramesz",
        "ramesz@email.com",
        "password");

    String stringified = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringified))
        .andExpect(status().isOk());
  }

  /**
   * Tests that trying to register the same user twice results in a bad request
   * response.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should not allow duplicate user registration")
  public void authControllerShouldNotRegister() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "ramesz",
        "ramesz@email.com",
        "password");

    String stringified = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringified))
        .andExpect(status().isOk());

    // Second registration attempt with the same data should fail
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringified))
        .andExpect(status().isBadRequest());
  }
        .andExpect(content().string("User already exists"));

  /**
   * Tests that after succesfully registered, user can successfully
   * login through the /api/v1/auth/login endpoint and return with a token.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should successfully login and return JWT token")
  public void authControllerShouldSuccessfullyLoginAndReturnWithToken() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "ramesz",
        "ramesz@email.com",
        "password");

    String stringifiedRegister = objectMapper.writeValueAsString(registerRequest);

    // First register the user
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedRegister))
        .andExpect(status().isOk());

    LoginRequest loginRequest = new LoginRequest(
        "ramesz@email.com",
        "password");

    String stringifiedLogin = objectMapper.writeValueAsString(loginRequest);

    // Then login with the registered user
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedLogin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.token").isString())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  /**
   * Tests that login fails with invalid credentials.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should fail login with invalid credentials")
  public void authControllerShouldFailLoginWithInvalidCredentials() throws Exception {

    LoginRequest loginRequest = new LoginRequest(
        "nonexistent@email.com",
        "wrongpassword");

    String stringifiedLogin = objectMapper.writeValueAsString(loginRequest);

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedLogin))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid credentials"));
  }

  /**
   * Tests that login fails with correct email but wrong password.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should fail login with wrong password")
  public void authControllerShouldFailLoginWithWrongPassword() throws Exception {

    // First register a user
    RegisterRequest registerRequest = new RegisterRequest(
        "testuser",
        "test@email.com",
        "correctpassword");

    String stringifiedRegister = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedRegister))
        .andExpect(status().isOk());

    // Try to login with wrong password
    LoginRequest loginRequest = new LoginRequest(
        "test@email.com",
        "wrongpassword");

    String stringifiedLogin = objectMapper.writeValueAsString(loginRequest);

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedLogin))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid credentials"));
  }

  /**
   * Tests registration with invalid JSON format.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should handle malformed JSON in registration")
  public void authControllerShouldHandleMalformedJson() throws Exception {

    String malformedJson = "{ \"username\": \"test\", \"email\": }";

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }

  /**
   * Tests registration with missing content type.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  @DisplayName("Should handle missing content type")
  public void authControllerShouldHandleMissingContentType() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "testuser",
        "test@email.com",
        "password");

    String stringified = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .content(stringified))
        .andExpect(status().isUnsupportedMediaType());
  }
}
