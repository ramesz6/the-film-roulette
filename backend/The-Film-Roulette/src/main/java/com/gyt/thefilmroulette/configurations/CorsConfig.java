package com.gyt.thefilmroulette.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration represents the CORS configurations for the application.
 */
@Configuration
@Getter
public class CorsConfig {
  @Value("${cors.urls}")
  private String corsUrls;
}
