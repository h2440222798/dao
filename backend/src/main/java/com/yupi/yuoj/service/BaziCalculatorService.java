package com.yupi.yuoj.service;

import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import java.time.LocalDate;
import java.util.Map;

/**
 * Solar-calendar Bazi calculator.
 */
public interface BaziCalculatorService {

    /**
     * Build the user's natal chart.
     */
    Map<String, Object> buildNatalChart(BaziAnalyzeRequest request);

    /**
     * Build today's daily clash and flow data.
     */
    Map<String, Object> buildTodayFlow(LocalDate today, Map<String, Object> natalChart);
}
