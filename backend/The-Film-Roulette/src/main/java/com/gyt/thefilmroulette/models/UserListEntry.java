package com.gyt.thefilmroulette.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a title in a user's list (watch later / seen / disliked).
 */
@Entity
@Table(
    name = "user_list_entry",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_media_tmdb",
        columnNames = {
          "user_id",
          "media_type",
          "tmdb_id"
        }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListEntry {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "tmdb_id", nullable = false)
  private int tmdbId;

  /**
   * TMDB media type: "movie" or "tv".
   */
  @Column(name = "media_type", nullable = false)
  private String mediaType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ListStatus status;
}
