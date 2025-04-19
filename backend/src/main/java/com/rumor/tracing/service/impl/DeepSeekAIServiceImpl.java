// DeepSeekAIServiceImpl.java
package com.rumor.tracing.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rumor.tracing.dto.request.DeepSeekAnalysisRequest;
import com.rumor.tracing.dto.response.DeepSeekAnalysisResponse;
import com.rumor.tracing.dto.DeepSeekSourceAnalysis;
import com.rumor.tracing.entity.DeepSeekAnalysis;
import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.model.AnalysisType;
import com.rumor.tracing.repository.DeepSeekAnalysisRepository;
import com.rumor.tracing.service.DeepSeekAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekAIServiceImpl implements DeepSeekAIService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    @Value("${ai.service.api-key}")
    private String apiKey;
    
    @Value("${ai.service.model}")
    private String model;

    @Value("${ai.service.temperature}")
    private double temperature;

    @Value("${ai.service.max-tokens}")
    private int maxTokens;

    @Value("${ai.service.timeout}")
    private int timeout;

    @Value("${ai.service.max-retries}")
    private int maxRetries;

    @Value("${ai.service.retry-delay}")
    private int retryDelay;

    @Value("${ai.service.frequency-penalty}")
    private double frequencyPenalty;

    @Value("${ai.service.presence-penalty}")
    private double presencePenalty;

    @Value("${ai.service.top-p}")
    private double topP;
    
    private final RestTemplate restTemplate;
    private final DeepSeekAnalysisRepository deepSeekAnalysisRepository;
    
    @PostConstruct
    public void init() {
        // 配置 RestTemplate 超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(300000); // 增加到 5 分钟 
        factory.setReadTimeout(300000);    // 增加到 5 分钟
        restTemplate.setRequestFactory(factory);
        
        // 配置消息转换器
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        
        // 配置字符串转换器，确保使用UTF-8编码
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        
        // 配置JSON转换器，确保使用UTF-8编码
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("application", "json", StandardCharsets.UTF_8),
                new MediaType("text", "plain", StandardCharsets.UTF_8),
                new MediaType("application", "*+json", StandardCharsets.UTF_8)
        ));
        converters.add(jsonConverter);
        
        restTemplate.setMessageConverters(converters);
    }
    
    @Override
    public RumorAnalysis analyzeText(String content) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(content);
        request.setType("TEXT");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeImage(String imageUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(imageUrl);
        request.setType("image");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeVideo(String videoUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(videoUrl);
        request.setType("video");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeMultiModal(String content, String imageUrl, String videoUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(content);
        request.setImageUrl(imageUrl);
        request.setVideoUrl(videoUrl);
        request.setType("multimodal");
        return analyzeContent(request);
    }
    
    @Override
    public DeepSeekAnalysisResponse analyzeRumor(DeepSeekAnalysisRequest request) {
        try {
            // 构建系统提示词
            String systemPrompt = "你是一个专业的谣言分析助手。请分析用户提供的信息，判断其真实性并给出分析依据。\n\n" +
                "你必须以 JSON 格式输出分析结果，格式如下：\n" +
                "{\n" +
                "  \"credibilityScore\": 0.8,  // 可信度评分(0-1)\n" +
                "  \"factCheckingPoints\": [  // 事实核查要点\n" +
                "    \"根据官方数据...\",\n" +
                "    \"经查证...\"\n" +
                "  ],\n" +
                "  \"misinformationIndicators\": [  // 虚假信息指标\n" +
                "    \"缺乏可靠来源\",\n" +
                "    \"数据不一致\"\n" +
                "  ],\n" +
                "  \"verificationRecommendation\": \"建议通过官方渠道核实\",  // 验证建议\n" +
                "  \"sourceAnalysis\": {\n" +
                "    \"reliability\": 0.9,  // 来源可靠性(0-1)\n" +
                "    \"reputation\": \"官方来源\",  // 来源类型\n" +
                "    \"concerns\": [  // 可疑点\n" +
                "      \"数据来源不明\",\n" +
                "      \"缺乏官方认证\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", request.getContent())
            ));
            requestBody.put("stream", false);
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("frequency_penalty", frequencyPenalty);
            requestBody.put("presence_penalty", presencePenalty);
            requestBody.put("top_p", topP);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/chat/completions",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                
                if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                    JsonNode choices = root.get("choices");
                    if (choices.get(0).has("message") && choices.get(0).get("message").has("content")) {
                        String content = choices.get(0).get("message").get("content").asText();
                        
                        // 解析 AI 返回的 JSON 内容
                        JsonNode analysisResult = mapper.readTree(content);
                        
                        // 构建响应对象
                        return DeepSeekAnalysisResponse.builder()
                            .credibilityScore(analysisResult.has("credibilityScore") ? 
                                analysisResult.get("credibilityScore").asDouble() : 0.0)
                            .factCheckingPoints(analysisResult.has("factCheckingPoints") ? 
                                mapper.convertValue(analysisResult.get("factCheckingPoints"), new TypeReference<List<String>>() {}) : 
                                Collections.emptyList())
                            .misinformationIndicators(analysisResult.has("misinformationIndicators") ? 
                                mapper.convertValue(analysisResult.get("misinformationIndicators"), new TypeReference<List<String>>() {}) : 
                                Collections.emptyList())
                            .verificationRecommendation(analysisResult.has("verificationRecommendation") ? 
                                analysisResult.get("verificationRecommendation").asText() : 
                                "暂无验证建议")
                            .sourceAnalysis(analysisResult.has("sourceAnalysis") ? 
                                mapper.convertValue(analysisResult.get("sourceAnalysis"), DeepSeekSourceAnalysis.class) : 
                                DeepSeekSourceAnalysis.builder()
                                    .reliability(0.0)
                                    .reputation("未知")
                                    .concerns(Collections.emptyList())
                                    .build())
                            .build();
                    }
                }
            }
            
            // 如果解析失败，返回默认响应
            return createDefaultResponse();
        } catch (Exception e) {
            log.error("分析谣言时发生错误", e);
            return createDefaultResponse();
        }
    }
    
    private DeepSeekAnalysisResponse createDefaultResponse() {
        return DeepSeekAnalysisResponse.builder()
            .credibilityScore(0.0)
            .factCheckingPoints(new ArrayList<>())
            .misinformationIndicators(new ArrayList<>())
            .verificationRecommendation("由于技术原因，无法提供分析结果")
            .sourceAnalysis(DeepSeekSourceAnalysis.builder()
                .reliability(0.0)
                .reputation("未知")
                .concerns(new ArrayList<>())
                .build())
            .build();
    }
    
    private RumorAnalysis analyzeContent(DeepSeekAnalysisRequest request) {
        DeepSeekAnalysisResponse response = analyzeRumor(request);
        
        // 保存分析结果
        DeepSeekAnalysis analysis = new DeepSeekAnalysis();
        analysis.setAnalysisType(request.getType());
        analysis.setResult(response.getVerificationRecommendation());
        analysis.setConfidence(response.getCredibilityScore());
        analysis = deepSeekAnalysisRepository.save(analysis);
        
        // 转换为RumorAnalysis
        RumorAnalysis result = new RumorAnalysis();
        result.setId(analysis.getId());
        result.setType(AnalysisType.valueOf(analysis.getAnalysisType()));
        result.setResult(analysis.getResult());
        result.setConfidence(analysis.getConfidence());
        result.setAnalyzedAt(analysis.getCreatedAt());
        
        // 设置其他字段
        result.setAnalysisModel(model);
        result.setAnalysisVersion("1.0");
        result.setAnalysisParameters("{}");
        result.setAnalysisDuration(0L);
        result.setStatus("COMPLETED");
        
        return result;
    }
}