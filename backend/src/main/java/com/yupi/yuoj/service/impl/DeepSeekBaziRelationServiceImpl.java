package com.yupi.yuoj.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.config.DeepSeekConfig;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.dto.bazi.BaziRelationAnalyzeRequest;
import com.yupi.yuoj.service.DeepSeekBaziRelationService;
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
 * DeepSeek analysis for relationship report.
 */
@Service
public class DeepSeekBaziRelationServiceImpl implements DeepSeekBaziRelationService {

    @Resource
    private DeepSeekConfig deepSeekConfig;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> analyze(BaziRelationAnalyzeRequest request, Map<String, Object> relationProfile) {
        if (StringUtils.isBlank(deepSeekConfig.getApiKey())) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek API key is not configured");
        }
        try {
            String content = callDeepSeek(request, relationProfile);
            return parse(content);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek relationship analysis failed: " + e.getMessage());
        }
    }

    private String callDeepSeek(BaziRelationAnalyzeRequest request, Map<String, Object> relationProfile)
            throws IOException {
        RestTemplate restTemplate = new RestTemplate(buildRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekConfig.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", StringUtils.defaultIfBlank(deepSeekConfig.getModel(), "deepseek-v4-pro"));
        payload.put("temperature", 0.25);
        payload.put("max_tokens", 2800);
        payload.put("response_format", mapOf("type", "json_object"));
        payload.put("messages", buildMessages(request, relationProfile));

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

    private List<Map<String, String>> buildMessages(BaziRelationAnalyzeRequest request, Map<String, Object> relationProfile)
            throws IOException {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system",
                "你是专业的八字关系与人生规划分析助手。"
                        + "必须基于结构化八字档案和关系画像做分析，不要虚构生日。"
                        + "输出只允许 JSON，不要 Markdown。"
                        + "关注人际关系、婚姻爱情、事业发展、财运、旺他的城市和行动建议。"));
        String userText = "请基于下面的关系画像输出完整报告。"
                + "字段要求：relationSummary, relationshipAdvice, marriageAdvice, careerAdvice, wealthAdvice, luckyCities, luckyDirections, fortunateElements, scores, riskNotice。"
                + "scores 必须含 relationship/marriage/career/wealth 四个 0-100 整数。"
                + "\n用户补充问题：" + StringUtils.defaultIfBlank(request.getFocusQuestion(), "未填写")
                + "\n当前城市：" + StringUtils.defaultIfBlank(request.getCurrentCity(), "未填写")
                + "\n关系状态：" + StringUtils.defaultIfBlank(request.getRelationshipStatus(), "unknown")
                + "\n事业阶段：" + StringUtils.defaultIfBlank(request.getCareerStage(), "unknown")
                + "\n结构化画像：" + objectMapper.writeValueAsString(relationProfile);
        messages.add(message("user", userText));
        return messages;
    }

    private Map<String, Object> parse(String content) throws IOException {
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
        Object raw = parsed.get("scores");
        if (!(raw instanceof Map)) {
            parsed.put("scores", defaultScores());
            return;
        }
        Map<String, Object> scores = (Map<String, Object>) raw;
        Map<String, Integer> normalized = new LinkedHashMap<>();
        String[] keys = {"relationship", "marriage", "career", "wealth"};
        int total = 0;
        String maxKey = "relationship";
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
        scores.put("relationship", 25);
        scores.put("marriage", 25);
        scores.put("career", 25);
        scores.put("wealth", 25);
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
