package com.gyt.thefilmroulette.controller;

import com.gyt.thefilmroulette.dtos.profile.ListEntryRequest;
import com.gyt.thefilmroulette.dtos.profile.ListEntryResponse;
import com.gyt.thefilmroulette.dtos.profile.PreferencesRequest;
import com.gyt.thefilmroulette.dtos.profile.PreferencesResponse;
import com.gyt.thefilmroulette.models.ListStatus;
import com.gyt.thefilmroulette.models.User;
import com.gyt.thefilmroulette.services.profile.UserProfileService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
public class MeController {

  private final UserProfileService userProfileService;

  @GetMapping("/preferences")
  public PreferencesResponse getPreferences(@AuthenticationPrincipal User user) {
    return userProfileService.getPreferences(user);
  }

  @PutMapping("/preferences")
  public PreferencesResponse updatePreferences(
      @AuthenticationPrincipal User user,
      @RequestBody PreferencesRequest request) {

    return userProfileService.updatePreferences(user, request);
  }

  @GetMapping("/list/watch-later")
  public List<ListEntryResponse> getWatchLater(@AuthenticationPrincipal User user) {
    return userProfileService.getList(user, ListStatus.WATCH_LATER);
  }

  @GetMapping("/list/seen")
  public List<ListEntryResponse> getSeen(@AuthenticationPrincipal User user) {
    return userProfileService.getList(user, ListStatus.SEEN);
  }

  @PutMapping("/list/watch-later")
  public ResponseEntity<Void> addWatchLater(
      @AuthenticationPrincipal User user,
      @RequestBody ListEntryRequest request) {

    userProfileService.setListStatus(user, ListStatus.WATCH_LATER, request);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/list/seen")
  public ResponseEntity<Void> addSeen(
      @AuthenticationPrincipal User user,
      @RequestBody ListEntryRequest request) {

    userProfileService.setListStatus(user, ListStatus.SEEN, request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/list/{mediaType}/{tmdbId}")
  public ResponseEntity<Void> remove(
      @AuthenticationPrincipal User user,
      @PathVariable String mediaType,
      @PathVariable int tmdbId) {

    userProfileService.removeFromList(user, mediaType, tmdbId);
    return ResponseEntity.noContent().build();
  }
}
