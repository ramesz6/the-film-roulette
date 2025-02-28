package com.gyt.thefilmroulette.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Tests the MovieApiController for the /api/v1/movie/discover endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MovieApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Tests the /api/v1/movie/discover endpoint to ensure it returns a valid
   * DiscoveryResponse.
   */
  @Test
  public void MovieApiControllerShouldReturnWithDiscoveryResponseJson() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/movie/discover"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    DiscoveryResponse response = objectMapper.readValue(jsonResponse, DiscoveryResponse.class);

    assertNotNull(response);
  }

}
