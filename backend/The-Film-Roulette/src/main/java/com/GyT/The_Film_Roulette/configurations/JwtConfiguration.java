package com.GyT.The_Film_Roulette.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for JWT (JSON Web Token) settings.
 * This class loads the JWT secret key from application properties.
 */
@Configuration
public class JwtConfiguration {

  /**
   * The secret key used for signing and verifying JWT tokens.
   * It is injected from the application properties file.
   */
  @Value("${jwt.secret}")
  public String jwtSecret;
}
