package com.GyT.The_Film_Roulette.configurations;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TMDBInterceptor implements Interceptor {

  @Value("${tmdb.api.key}")
  private String apiKey;

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request newRequest = originalRequest.newBuilder()
        .addHeader("Authorization", "Bearer " + apiKey)
        .build();
    System.out.println(apiKey);
    return chain.proceed(newRequest);
  }
}
