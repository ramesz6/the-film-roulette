package com.gyt.thefilmroulette.configurations;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Configuration class for setting up the Retrofit instance.
 * This class initializes a Retrofit client with a base URL and an OkHttpClient,
 * which includes an interceptor for handling API requests.
 */
@Configuration
@AllArgsConstructor
public class RetrofitConfig {

  private static final String BASE_URL = "https://api.themoviedb.org/3/";
  private final MovieApiInterceptor interceptor;

  @Bean
  public Retrofit retrofit() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();

    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build();
  }
}