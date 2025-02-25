package com.gyt.thefilmroulette.security;

import com.gyt.thefilmroulette.configurations.CorsConfig;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the application, setting up authentication and
 * authorization.
 * Configures basic security settings including CSRF, request authorization, and
 * login options.
 * <p>
 * This class also configures CORS (Cross-Origin Resource Sharing) to allow
 * specific URLs and methods.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  private final CorsConfig corsConfig;

  /**
   * A list of allowed paths that do not require authentication.
   * <p>
   * These paths are exempt from security measures, meaning anyone can access
   * them without authentication.
   */
  private static String[] ALLOW_LIST = { "/api/v1/auth/**", "/api/v1/movie/**" };

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http the {@link HttpSecurity} instance to configure
   * 
   * @return the configured {@link SecurityFilterChain} instance
   * 
   * @throws Exception if an error occurs during the configuration
   * 
   *                   <p>
   *                   This method disables CSRF protection, permits access to the
   *                   paths listed in {@link ALLOW_LIST}, and enables basic HTTP
   *                   and
   *                   form-based login.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req -> req.requestMatchers(ALLOW_LIST)
            .permitAll())
        .httpBasic(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }

  /**
   * Configures the CORS settings for the application.
   * <p>
   * This configuration allows specific origins, methods, headers, and credentials
   * to be shared between the client and server.
   * 
   * @return the {@link CorsConfigurationSource} used to handle CORS requests
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.stream(corsConfig.getCorsUrls().split(",")).toList());
    configuration
        .setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS",
            "HEAD"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
