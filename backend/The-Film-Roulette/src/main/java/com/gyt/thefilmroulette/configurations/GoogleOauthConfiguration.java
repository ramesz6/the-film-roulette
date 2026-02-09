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
  private String clientId;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
}
