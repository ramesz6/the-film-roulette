package com.gyt.thefilmroulette.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ElementCollection
  @Builder.Default
  private List<Integer> likedGenreIds = new ArrayList<>();

  private Integer yearFrom;

  private Integer yearTo;

  @Builder.Default
  private boolean includeMovies = true;

  @Builder.Default
  private boolean includeSeries = false;
}
