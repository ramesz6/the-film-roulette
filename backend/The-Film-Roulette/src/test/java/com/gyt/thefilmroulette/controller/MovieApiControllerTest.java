/**
 * Tests for the movie API controller endpoints.
 */
package com.gyt.thefilmroulette.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MovieApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static MockWebServer mockWebServer;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    registry.add("tmdb.api.base-url", () -> mockWebServer.url("/").toString());
  }

  @AfterAll
  static void tearDown() throws Exception {
    if (mockWebServer != null) {
      mockWebServer.shutdown();
    }
  }

  @Test
  void testMovieApiControllerReturnsDiscoveryResponse() throws Exception {
    String mockJson = "{\"page\":1,\"results\":[]}";
    mockWebServer.enqueue(new MockResponse()
        .setBody(mockJson)
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json"));

    MvcResult result = mockMvc.perform(get("/api/v1/movie/discover"))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    DiscoveryResponse response = objectMapper.readValue(jsonResponse, DiscoveryResponse.class);

    assertNotNull(response);
  }
}
