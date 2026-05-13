package com.yupi.yuoj.service;

import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import java.util.Map;

/**
 * DeepSeek-backed interpretation service.
 */
public interface DeepSeekBaziService {

    /**
     * Generate a structured Bazi and constitution interpretation.
     */
    Map<String, Object> analyze(BaziAnalyzeRequest request, Map<String, Object> natalChart, Map<String, Object> todayFlow);
}
