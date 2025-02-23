package com.gyt.thefilmroulette.configurations;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Configuration class for setting up the Retrofit instance.
 * This class initializes a Retrofit client with a base URL and an OkHttpClient,
 * which includes an interceptor for handling API requests.
 * The interceptor is responsible for adding authentication tokens or modifying
 * the request headers as necessary.
 */
@Configuration
@AllArgsConstructor
public class RetrofitConfig {

  private static final String BASE_URL = "https://api.themoviedb.org/3/";
  private final MovieApiInterceptor interceptor;

  /**
   * Configures and returns a Retrofit instance.
   * This method builds a Retrofit client using a base URL, a Gson converter for
   * JSON parsing, and an OkHttpClient with an interceptor for API requests.
   * 
   * @return the configured Retrofit instance
   */
  @Bean
  public Retrofit retrofit() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor) // Add the custom interceptor
        .build();

    return new Retrofit.Builder()
        .baseUrl(BASE_URL) // Set the base URL for the API
        .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
        .client(client) // Set the OkHttpClient with the interceptor
        .build(); // Build and return the Retrofit instance
  }
}