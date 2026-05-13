package com.yupi.yuoj.service;

import com.yupi.yuoj.model.dto.bazi.BaziRelationAnalyzeRequest;
import java.util.Map;

/**
 * DeepSeek service for relationship report.
 */
public interface DeepSeekBaziRelationService {

    Map<String, Object> analyze(BaziRelationAnalyzeRequest request, Map<String, Object> relationProfile);
}
