package com.yupi.yuoj.service;

import com.yupi.yuoj.model.entity.UserBaziProfile;
import java.util.Map;

/**
 * Build relationship analysis payload from natal chart.
 */
public interface BaziRelationStrategyService {

    Map<String, Object> buildRelationProfile(UserBaziProfile profile, String currentCity, String relationshipStatus,
            String careerStage, String focusQuestion);
}
