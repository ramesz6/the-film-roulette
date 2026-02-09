package com.gyt.thefilmroulette.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration class for Google OAuth settings.
 * Loads the Google OAuth client ID from application properties.
 */
@ConfigurationProperties(prefix = "google.oauth")
public class GoogleOauthConfiguration {

  /**
   * The Google OAuth client ID used to validate Google ID tokens.
   */
  public String clientId;
}
