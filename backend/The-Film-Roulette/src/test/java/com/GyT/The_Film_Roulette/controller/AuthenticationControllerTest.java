package com.GyT.The_Film_Roulette.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void AuthControllerShouldSuccesfullyRegister() throws Exception {

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

  @Test
  public void AuthControllerShouldUnsuccesfullyRegister() throws Exception {

    RegisterRequest registerRequest = new RegisterRequest(
        "ramesz",
        "ramesz@email.com",
        "password");

    String stringified = objectMapper.writeValueAsString(registerRequest);

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringified))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(stringified))
        .andExpect(status().isBadRequest());
  }
}
