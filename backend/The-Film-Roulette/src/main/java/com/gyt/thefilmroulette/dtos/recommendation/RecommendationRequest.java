package com.gyt.thefilmroulette.dtos.recommendation;

import java.util.List;

/**
 * Request DTO used to exclude recommendation keys.
 */
public record RecommendationRequest(List<String> exclude) {
}
