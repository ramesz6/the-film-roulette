package com.gyt.thefilmroulette.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Google OAuth settings.
 * Loads the Google OAuth client ID from application properties.
 */
@Configuration
public class GoogleOAuthConfiguration {

  /**
   * The Google OAuth client ID used to validate Google ID tokens.
   */
  @Value("${google.oauth.client-id:}")
  public String clientId;
}
