package com.yupi.yuoj.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.config.DeepSeekConfig;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.dto.divination.DivinationAnalyzeRequest;
import com.yupi.yuoj.service.DeepSeekDivinationService;
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
 * DeepSeek API implementation for Daoist lot drawing and Liuyao interpretation.
 */
@Service
public class DeepSeekDivinationServiceImpl implements DeepSeekDivinationService {

    @Resource
    private DeepSeekConfig deepSeekConfig;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> analyze(DivinationAnalyzeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(deepSeekConfig.getApiKey())) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek API key is not configured");
        }
        try {
            String content = callDeepSeek(request);
            return parseModelJson(content);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "DeepSeek divination failed: " + e.getMessage());
        }
    }

    private String callDeepSeek(DivinationAnalyzeRequest request) throws IOException {
        RestTemplate restTemplate = new RestTemplate(buildRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekConfig.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", StringUtils.defaultIfBlank(deepSeekConfig.getModel(), "deepseek-v4-pro"));
        payload.put("temperature", 0.35);
        payload.put("max_tokens", 3000);
        payload.put("response_format", mapOf("type", "json_object"));
        payload.put("messages", buildMessages(request));

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

    private List<Map<String, String>> buildMessages(DivinationAnalyzeRequest request) throws IOException {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system",
                "你是道家签卦、易经六爻与五行养生文化解读助手。"
                        + "你要把签文和六爻解释成清晰、克制、可执行的生活建议。"
                        + "不要承诺确定未来，不要制造恐惧，不要给医疗诊断、投资指令或违法建议。"
                        + "健康部分只能做作息、饮食、情绪和就医提醒；财富部分只能做风险意识和行动建议。"
                        + "输出只允许 JSON，不要 Markdown。"));

        String userText = "请根据以下今日签与六爻起卦信息，生成求签问卜综合分析。"
                + "必须返回 JSON 字段：signText, overall, categories, liuyao, comprehensive, actionPlan, riskNotice。"
                + "categories 必须含 health, relationship, career, wealth, luck 五项；每项包含 score(0-100整数), title, analysis, advice。"
                + "liuyao 必须包含 question, hexagram, changedHexagram, movingLines, answer, evidence, timing, risk。"
                + "comprehensive 必须包含 summary, favorableActions, avoidActions, luckyDirection, luckyColor, wuxingSupport。"
                + "actionPlan 是 3-5 条可执行建议。"
                + "\n今日/起卦信息：" + objectMapper.writeValueAsString(request)
                + "\n如果 question 为空，liuyao.answer 请说明本次主要看今日总运，并给出泛用提醒。";
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
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
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
