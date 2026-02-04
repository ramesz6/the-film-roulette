/**
 * Request DTO used to exclude already-seen recommendation keys.
 */
package com.gyt.thefilmroulette.dtos.recommendation;

import java.util.List;

public record RecommendationRequest(List<String> exclude) {
}
