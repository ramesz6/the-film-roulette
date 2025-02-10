package com.GyT.The_Film_Roulette.controller;

import com.GyT.The_Film_Roulette.dtos.login.LoginRequest;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

  /**
   * Tests that after succesfully registered, user can successfully
   * login through the /api/v1/auth/login endpoint and return with a token.
   *
   * @throws Exception if there is an error in performing the test
   */
  @Test
  public void authControllerShouldSuccessfullyLoginAndReturnWithToken() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "ramesz",
        "ramesz@email.com",
        "password");

    String stringifiedRegister = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedRegister))
        .andExpect(status().isOk());

    LoginRequest loginRequest = new LoginRequest(
        "ramesz@email.com",
        "password");

    String stringifiedLogin = objectMapper.writeValueAsString(loginRequest);

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringifiedLogin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
  }
}
