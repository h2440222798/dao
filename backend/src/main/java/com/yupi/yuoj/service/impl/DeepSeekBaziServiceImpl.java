package com.yupi.yuoj.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.config.DeepSeekConfig;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.dto.bazi.BaziAnalyzeRequest;
import com.yupi.yuoj.service.DeepSeekBaziService;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * DeepSeek API implementation for Bazi interpretation.
 */
@Service
public class DeepSeekBaziServiceImpl implements DeepSeekBaziService {

    @Resource
    private DeepSeekConfig deepSeekConfig;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> analyze(BaziAnalyzeRequest request, Map<String, Object> natalChart,
            Map<String, Object> todayFlow) {
        if (StringUtils.isBlank(deepSeekConfig.getApiKey())) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek API key is not configured");
        }
        try {
            String content = callDeepSeek(request, natalChart, todayFlow);
            return parseModelJson(content);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek analysis failed: " + e.getMessage());
        }
    }

    private String callDeepSeek(BaziAnalyzeRequest request, Map<String, Object> natalChart,
            Map<String, Object> todayFlow) throws IOException {
        RestTemplate restTemplate = new RestTemplate(buildRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekConfig.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", StringUtils.defaultIfBlank(deepSeekConfig.getModel(), "deepseek-v4-pro"));
        payload.put("temperature", 0.2);
        payload.put("max_tokens", 2600);
        payload.put("response_format", mapOf("type", "json_object"));
        payload.put("messages", buildMessages(request, natalChart, todayFlow));

        String url = StringUtils.removeEnd(deepSeekConfig.getBaseUrl(), "/") + "/chat/completions";
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(payload), headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(response.getBody())) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek returned empty response");
        }
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || StringUtils.isBlank(contentNode.asText())) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek response has no content");
        }
        return contentNode.asText();
    }

    private SimpleClientHttpRequestFactory buildRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeout = deepSeekConfig.getTimeoutMs() == null ? 60000 : deepSeekConfig.getTimeoutMs();
        factory.setConnectTimeout((int) Duration.ofMillis(timeout).toMillis());
        factory.setReadTimeout((int) Duration.ofMillis(timeout).toMillis());
        return factory;
    }

    private List<Map<String, String>> buildMessages(BaziAnalyzeRequest request, Map<String, Object> natalChart,
            Map<String, Object> todayFlow) throws IOException {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system",
                "你是严谨的八字命理与中医五行体质分析助手。"
                        + "必须基于给定四柱、十神、五行分布和今日流日冲克分析，不要重新虚构出生信息。"
                        + "输出只允许 JSON，不要 Markdown。"
                        + "健康建议只能做养生参考，不能诊断疾病，遇到明显不适应建议就医。"));
        String userText = "请分析这个用户的个人八字、五行体质和今日冲克。"
                + "要求：1. 命理分析细致但克制，明确日主、格局倾向、五行强弱；"
                + "2. 体质分析要映射肝心脾肺肾与作息饮食情绪；"
                + "3. 给出今天的冲克提醒和可执行建议；"
                + "4. 返回 JSON 字段：constitutionSummary, healthAdvice, aiAnalysis, todayAdvice, riskNotice, scores。"
                + "scores 必须含 wood/fire/earth/metal/water 五个 0-100 整数且总和为 100。"
                + "\n用户补充体感：" + StringUtils.defaultIfBlank(request.getBodySignals(), "未填写")
                + "\n结构化八字：" + objectMapper.writeValueAsString(natalChart)
                + "\n今日流日：" + objectMapper.writeValueAsString(todayFlow);
        messages.add(message("user", userText));
        return messages;
    }

    private Map<String, Object> parseModelJson(String content) throws IOException {
        String json = content.trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start >= 0 && end > start) {
            json = json.substring(start, end + 1);
        }
        Map<String, Object> parsed = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        normalizeScores(parsed);
        return parsed;
    }

    @SuppressWarnings("unchecked")
    private void normalizeScores(Map<String, Object> parsed) {
        Object rawScores = parsed.get("scores");
        if (!(rawScores instanceof Map)) {
            parsed.put("scores", defaultScores());
            return;
        }
        Map<String, Object> scores = (Map<String, Object>) rawScores;
        Map<String, Integer> normalized = new LinkedHashMap<>();
        String[] keys = {"wood", "fire", "earth", "metal", "water"};
        int total = 0;
        String maxKey = "wood";
        for (String key : keys) {
            int value = toScore(scores.get(key));
            normalized.put(key, value);
            total += value;
            if (value > normalized.get(maxKey)) {
                maxKey = key;
            }
        }
        if (total <= 0) {
            parsed.put("scores", defaultScores());
            return;
        }
        if (total != 100) {
            normalized.put(maxKey, Math.max(0, normalized.get(maxKey) + 100 - total));
        }
        parsed.put("scores", normalized);
    }

    private int toScore(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            int score = (int) Math.round(Double.parseDouble(String.valueOf(value)));
            return Math.max(0, Math.min(100, score));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Map<String, Integer> defaultScores() {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("wood", 20);
        scores.put("fire", 20);
        scores.put("earth", 20);
        scores.put("metal", 20);
        scores.put("water", 20);
        return scores;
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private Map<String, Object> mapOf(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
}
