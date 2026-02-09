package com.gyt.thefilmroulette.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Configuration for decoding and verifying Google ID tokens.
 */
@Configuration
public class GoogleJwtDecoderConfig {

  /**
   * Creates a {@link JwtDecoder} that verifies tokens against Google's public
   * JWKS endpoint.
   *
   * @return Google ID token decoder
   */
  @Bean
  public JwtDecoder googleJwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build();
  }
}
