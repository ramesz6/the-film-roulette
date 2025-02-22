package com.GyT.The_Film_Roulette.models;

import java.util.Collection;
import java.util.List;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user entity in the system.
 * Implements {@link UserDetails} interface to be used for authentication and
 * authorization purposes.
 * This class contains user credentials, such as email, username, and password.
 */
@Entity(name = "_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  /**
   * Unique identifier for the user.
   */
  @Id
  @GeneratedValue
  private Long id;

  /**
   * Unique email of the user. This is also used for authentication.
   */
  @Column(unique = true)
  @NaturalId(mutable = true)
  private String email;

  /**
   * Unique username of the user.
   */
  @Column(unique = true)
  private String username;

  /**
   * Password for the user account.
   */
  private String password;

  /**
   * Grants authorities (roles) to the user.
   *
   * @return a collection of granted authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("USER"));
  }

  /**
   * Indicates whether the account has expired.
   *
   * @return true if the account is non-expired, otherwise false
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the account is locked.
   *
   * @return true if the account is non-locked, otherwise false
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials have expired.
   *
   * @return true if the credentials are non-expired, otherwise false
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   *
   * @return true if the user is enabled, otherwise false
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Retrieves the username for authentication purposes.
   *
   * @return the username of the user
   */
  @Override
  public String getUsername() {
    return this.username;
  }

  /**
   * Retrieves the email address for authentication purposes.
   *
   * @return the email of the user
   */
  public String getEmail() {
    return this.email;
  }
}