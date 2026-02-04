package com.gyt.thefilmroulette.controller;

import com.gyt.thefilmroulette.dtos.profile.ListEntryRequest;
import com.gyt.thefilmroulette.dtos.profile.ListEntryResponse;
import com.gyt.thefilmroulette.dtos.profile.PreferencesRequest;
import com.gyt.thefilmroulette.dtos.profile.PreferencesResponse;
import com.gyt.thefilmroulette.dtos.recommendation.RecommendationResponse;
import com.gyt.thefilmroulette.models.ListStatus;
import com.gyt.thefilmroulette.models.User;
import com.gyt.thefilmroulette.services.profile.UserProfileService;
import com.gyt.thefilmroulette.services.recommendation.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for the authenticated user's profile, lists, and roulette
 * recommendations.
 */
@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
public class MeController {

  private final UserProfileService userProfileService;
  private final RecommendationService recommendationService;

  /**
   * Returns the current user's saved preference settings.
   *
   * @param user authenticated user
   *
   * @return current preference settings
   */
  @GetMapping("/preferences")
  public PreferencesResponse getPreferences(@AuthenticationPrincipal User user) {
    return userProfileService.getPreferences(user);
  }

  /**
   * Updates the current user's preference settings.
   *
   * @param user    authenticated user
   * @param request preferences payload
   *
   * @return updated preference settings
   */
  @PutMapping("/preferences")
  public PreferencesResponse updatePreferences(
      @AuthenticationPrincipal User user,
      @RequestBody PreferencesRequest request) {

    return userProfileService.updatePreferences(user, request);
  }

  /**
   * Returns the current user's watch-later list.
   *
   * @param user authenticated user
   *
   * @return list entries
   */
  @GetMapping("/list/watch-later")
  public List<ListEntryResponse> getWatchLater(@AuthenticationPrincipal User user) {
    return userProfileService.getList(user, ListStatus.WATCH_LATER);
  }

  /**
   * Returns the current user's seen list.
   *
   * @param user authenticated user
   *
   * @return list entries
   */
  @GetMapping("/list/seen")
  public List<ListEntryResponse> getSeen(@AuthenticationPrincipal User user) {
    return userProfileService.getList(user, ListStatus.SEEN);
  }

  /**
   * Returns the current user's disliked list.
   *
   * @param user authenticated user
   *
   * @return list entries
   */
  @GetMapping("/list/disliked")
  public List<ListEntryResponse> getDisliked(@AuthenticationPrincipal User user) {
    return userProfileService.getList(user, ListStatus.DISLIKED);
  }

  /**
   * Adds or updates a title in the current user's watch-later list.
   *
   * @param user    authenticated user
   * @param request list entry payload
   *
   * @return 204 No Content
   */
  @PutMapping("/list/watch-later")
  public ResponseEntity<Void> addWatchLater(
      @AuthenticationPrincipal User user,
      @RequestBody ListEntryRequest request) {

    userProfileService.setListStatus(user, ListStatus.WATCH_LATER, request);
    return ResponseEntity.noContent().build();
  }

  /**
   * Adds or updates a title in the current user's seen list.
   *
   * @param user    authenticated user
   * @param request list entry payload
   *
   * @return 204 No Content
   */
  @PutMapping("/list/seen")
  public ResponseEntity<Void> addSeen(
      @AuthenticationPrincipal User user,
      @RequestBody ListEntryRequest request) {

    userProfileService.setListStatus(user, ListStatus.SEEN, request);
    return ResponseEntity.noContent().build();
  }

  /**
   * Adds or updates a title in the current user's disliked list.
   *
   * @param user    authenticated user
   * @param request list entry payload
   *
   * @return 204 No Content
   */
  @PutMapping("/list/disliked")
  public ResponseEntity<Void> addDisliked(
      @AuthenticationPrincipal User user,
      @RequestBody ListEntryRequest request) {

    userProfileService.setListStatus(user, ListStatus.DISLIKED, request);
    return ResponseEntity.noContent().build();
  }

  /**
   * Removes a title from the current user's lists.
   *
   * @param user      authenticated user
   * @param mediaType TMDB media type (movie or tv)
   * @param tmdbId    TMDB id
   *
   * @return 204 No Content
   */
  @DeleteMapping("/list/{mediaType}/{tmdbId}")
  public ResponseEntity<Void> remove(
      @AuthenticationPrincipal User user,
      @PathVariable String mediaType,
      @PathVariable int tmdbId) {

    userProfileService.removeFromList(user, mediaType, tmdbId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Returns the next roulette recommendation for the current user.
   *
   * @param user    authenticated user
   * @param exclude client-provided exclude keys
   *
   * @return recommendation
   */
  @GetMapping("/recommendation/next")
  public RecommendationResponse nextRecommendation(
      @AuthenticationPrincipal User user,
      @RequestParam(required = false) List<String> exclude) {

    return recommendationService.next(user, exclude);
  }
}
