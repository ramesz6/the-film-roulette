import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gyt.thefilmroulette.dtos.DiscoveryResponse;
import com.gyt.thefilmroulette.services.api.MovieApiService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests the MovieApiController for the /api/v1/movie/discover endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class MovieApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private MovieApiService movieApiService;

  /**
   * Tests the /api/v1/movie/discover endpoint to ensure it returns the correct
   * JSON structure and status.
   */
  @Test
  public void MovieApiControllerShouldReturnWithDiscoveryResponseJson() throws Exception {
    DiscoveryResponse mockResponse = new DiscoveryResponse(1, List.of());
    when(movieApiService.getResult()).thenReturn(mockResponse);

    mockMvc.perform(get("/api/v1/movie/discover"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.results").isArray());

    System.out.println("Test of Discovery endpoint has run.");
  }
}
