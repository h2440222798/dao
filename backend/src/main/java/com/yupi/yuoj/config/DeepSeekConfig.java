package com.yupi.yuoj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "deepseek")
@Data
public class DeepSeekConfig {

    /**
     * Compatible chat completions base URL.
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * API key. Prefer DEEPSEEK_API_KEY in production.
     */
    private String apiKey;

    /**
     * DeepSeek model for Bazi interpretation.
     */
    private String model = "deepseek-v4-pro";

    /**
     * Request timeout in milliseconds.
     */
    private Integer timeoutMs = 60000;
}
