package com.yupi.yuoj.service;

import com.yupi.yuoj.model.dto.divination.DivinationAnalyzeRequest;
import java.util.Map;

/**
 * DeepSeek-backed divination service.
 */
public interface DeepSeekDivinationService {

    /**
     * Generate daily lot and Liuyao interpretation.
     */
    Map<String, Object> analyze(DivinationAnalyzeRequest request);
}
