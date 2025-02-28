package com.gyt.thefilmroulette.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

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
