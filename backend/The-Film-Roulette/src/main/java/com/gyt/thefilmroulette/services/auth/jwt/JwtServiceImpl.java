package com.gyt.thefilmroulette.services.auth.jwt;

import com.gyt.thefilmroulette.configurations.JwtConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Implementation of the JwtService interface for generating JWT tokens.
 * 
 * <p>
 * This service uses the JwtConfiguration to obtain a secret key for signing the
 * JWTs.
 * It supports generating a JWT token based on user details.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final JwtConfiguration jwtConfiguration;

  /**
   * {@inheritDoc}
   * 
   * Generates a JWT token for the given user details.
   * 
   * @param userDetails The details of the user for which the JWT will be
   *                    generated.
   * @return The generated JWT token as a String.
   */
  @Override
  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername()) // Subject of the token (email in this app)
        .signWith(getSignInKey()) // Signing the token with a secret key
        .compact(); // Return the compact serialized JWT token
  }

  @Override
  public String extractSubject(String token) {
    return extractAllClaims(token).getSubject();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    String subject = extractSubject(token);
    return subject != null && subject.equals(userDetails.getUsername());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfiguration.jwtSecret)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Helper method to retrieve the signing key from the configuration.
   * 
   * <p>
   * This method decodes the base64 encoded secret key and returns it as a
   * {@link Key}
   * for signing JWTs.
   * </p>
   * 
   * @return The signing key used to sign the JWT.
   */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtConfiguration.jwtSecret); // Decode the base64 secret key

    return Keys.hmacShaKeyFor(keyBytes); // Generate HMAC SHA key from the decoded bytes
  }

  /**
   * {@inheritDoc}
   * 
   * This method is not implemented in the current version and throws an
   * exception.
   * 
   * @param userDetails The details of the user.
   * @param additional  Additional parameters to include in the token.
   * @throws UnsupportedOperationException Thrown because the method is not
   *                                       implemented.
   */
  @Override
  public String generateToken(UserDetails userDetails, Map<String, Objects> additional) {
    throw new UnsupportedOperationException("Unimplemented method 'generateToken'");
  }
}
