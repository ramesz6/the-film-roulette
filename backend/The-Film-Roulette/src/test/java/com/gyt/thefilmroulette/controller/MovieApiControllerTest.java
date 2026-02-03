package com.gyt.thefilmroulette.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.services.api.MovieApiService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class MovieApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MovieApiService movieApiService;

  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    // 🔹 A MovieApiService-nek a MockWebServer URL-jét adjuk meg
    String mockUrl = mockWebServer.url("/").toString();
    ReflectionTestUtils.setField(movieApiService, "tmdbApiBaseUrl", mockUrl);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
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
