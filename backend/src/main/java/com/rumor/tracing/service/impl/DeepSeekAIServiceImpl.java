// DeepSeekAIServiceImpl.java
package com.rumor.tracing.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount < maxRetries) {
            try {
                // 构建系统提示词
                String systemPrompt = "你是一个专业的谣言分析助手，专门负责分析教育相关信息的真实性。请对提供的内容进行深入分析，并给出详细的分析报告。\n\n" +
                    "你必须严格按照指定的 JSON 格式输出分析结果。\n\n" +
                    "分析要求：\n" +
                    "1. 信息可信度评估：\n" +
                    "   - 基于官方政策、法规和文件\n" +
                    "   - 考虑教育发展规律和趋势\n" +
                    "   - 评估信息的合理性和逻辑性\n" +
                    "2. 信息来源分析：\n" +
                    "   - 评估消息来源的权威性\n" +
                    "   - 判断信息传播渠道的可靠性\n" +
                    "   - 识别可能的误导或虚假信息特征\n" +
                    "3. 事实核查重点：\n" +
                    "   - 对比现有教育政策和规定\n" +
                    "   - 查证相关机构的官方声明\n" +
                    "   - 分析信息的时效性和适用性\n" +
                    "4. 提供具体的验证建议：\n" +
                    "   - 列出可查证的官方渠道\n" +
                    "   - 建议具体的核实方法\n" +
                    "   - 提供相关参考依据\n\n" +
                    "你必须以 JSON 格式输出分析结果，格式如下：\n" +
                    "{\n" +
                    "  \"credibilityScore\": 0.2,\n" +
                    "  \"factCheckingPoints\": [\n" +
                    "    \"根据《高等教育法》第X条规定...\",\n" +
                    "    \"教育部于2023年发布的《关于高等教育改革的指导意见》明确指出...\",\n" +
                    "    \"该信息与目前高等教育发展趋势不符，原因是...\"\n" +
                    "  ],\n" +
                    "  \"misinformationIndicators\": [\n" +
                    "    \"信息缺乏具体的政策依据和文件支持\",\n" +
                    "    \"消息来源不明确，未提供可验证的官方渠道\",\n" +
                    "    \"内容与现行教育政策存在明显冲突\"\n" +
                    "  ],\n" +
                    "  \"verificationRecommendation\": \"建议通过以下途径核实：\\n1. 访问教育部官网(www.moe.gov.cn)查询相关政策文件\\n2. 关注各高校官方公告\\n3. 查阅国家教育发展规划纲要\",\n" +
                    "  \"sourceAnalysis\": {\n" +
                    "    \"reliability\": 0.3,\n" +
                    "    \"reputation\": \"未经证实的网络传闻\",\n" +
                    "    \"concerns\": [\n" +
                    "      \"信息发布渠道不规范，缺乏官方背书\",\n" +
                    "      \"未提供具体的政策依据和实施细则\",\n" +
                    "      \"可能造成不必要的社会恐慌和误解\"\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}\n\n" +
                    "注意事项：\n" +
                    "1. 必须严格按照上述 JSON 格式输出，不要使用其他格式\n" +
                    "2. 评分必须客观公正，基于具体事实和依据\n" +
                    "3. 分析内容要具体详实，避免模糊表述\n" +
                    "4. 核查要点要有针对性，引用具体政策法规\n" +
                    "5. 验证建议要实用可行，提供明确途径\n" +
                    "6. 如果信息可信，要说明具体依据；如果可疑，要指出具体疑点";

                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-chat");
                requestBody.put("messages", Arrays.asList(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", request.getContent())
                ));
                // 降低 temperature 以获得更确定的回答
                requestBody.put("temperature", 0.3);
                requestBody.put("max_tokens", 3000);
                requestBody.put("stream", false);
                requestBody.put("response_format", Map.of("type", "json_object"));

                // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + apiKey);
                headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
                log.info("Sending request to DeepSeek API. Attempt #{}, Content: {}", retryCount + 1, request.getContent());
                
            ResponseEntity<String> response = restTemplate.exchange(
                    aiServiceUrl + "/chat/completions",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
                log.info("Received response from DeepSeek API. Status: {}, Body length: {}", 
                    response.getStatusCode(), response.getBody() != null ? response.getBody().length() : 0);

                // 如果返回状态不是200，重试
                if (response.getStatusCode() != HttpStatus.OK) {
                    log.warn("API request failed with status: {}, retrying... ({}/{})", 
                        response.getStatusCode(), retryCount + 1, maxRetries);
                    retryCount++;
                    
                    if (retryCount < maxRetries) {
                        // 等待重试
                        Thread.sleep(retryDelay);
                        continue;
                    } else {
                        throw new RuntimeException("API request failed with status: " + response.getStatusCode());
                    }
                }
                
                // 检查响应是否包含必要的数据
                if (response.getBody() == null || response.getBody().isEmpty()) {
                    log.warn("Empty response body, retrying... ({}/{})", retryCount + 1, maxRetries);
                    retryCount++;
                    
                    if (retryCount < maxRetries) {
                        Thread.sleep(retryDelay);
                        continue;
                    } else {
                        throw new RuntimeException("Empty response from API");
                    }
                }

                // 解析响应
                try {
                    DeepSeekAnalysisResponse analysisResponse = parseDeepSeekResponse(response.getBody());
                    validateAnalysisResponse(analysisResponse);
                    return analysisResponse;
                } catch (Exception e) {
                    log.error("Failed to parse response: {}", e.getMessage());
                    lastException = e;
                    retryCount++;
                    
                    if (retryCount < maxRetries) {
                        Thread.sleep(retryDelay);
                        continue;
                    } else {
                        throw e;
                    }
                }
        } catch (Exception e) {
                log.error("Error during API request: {}", e.getMessage());
                lastException = e;
                retryCount++;
                
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                } else {
                    break;
                }
            }
        }
        
        // 如果所有重试都失败，则返回包含默认值的响应
        log.error("All {} retry attempts failed. Last exception: {}", maxRetries, lastException != null ? lastException.getMessage() : "Unknown");
        
        DeepSeekSourceAnalysis sourceAnalysis = DeepSeekSourceAnalysis.builder()
            .reliability(0.0)
            .reputation("未知")
            .concerns(new ArrayList<>())
            .build();
            
        return DeepSeekAnalysisResponse.builder()
            .credibilityScore(0.0)
            .factCheckingPoints(new ArrayList<>())
            .misinformationIndicators(new ArrayList<>())
            .verificationRecommendation("由于分析服务暂时不可用，请稍后再试")
            .sourceAnalysis(sourceAnalysis)
            .build();
    }
    
    private void validateAnalysisResponse(DeepSeekAnalysisResponse response) {
        if (response == null) {
            throw new RuntimeException("Analysis response is null");
        }
        
        // 设置默认值
        if (response.getCredibilityScore() == null) {
            response.setCredibilityScore(0.0);
        }
        
        if (response.getFactCheckingPoints() == null) {
            response.setFactCheckingPoints(new ArrayList<>());
        }
        
        if (response.getMisinformationIndicators() == null) {
            response.setMisinformationIndicators(new ArrayList<>());
        }
        
        if (response.getVerificationRecommendation() == null || response.getVerificationRecommendation().isEmpty()) {
            response.setVerificationRecommendation("暂无验证建议");
        }
        
        if (response.getSourceAnalysis() == null) {
            DeepSeekSourceAnalysis sourceAnalysis = new DeepSeekSourceAnalysis();
            sourceAnalysis.setReliability(0.0);
            sourceAnalysis.setReputation("未知");
            sourceAnalysis.setConcerns(new ArrayList<>());
            response.setSourceAnalysis(sourceAnalysis);
        }
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
        
        return result;
    }
    
    private DeepSeekAnalysisResponse parseDeepSeekResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            
            // 验证响应格式
            if (!root.has("choices") || !root.get("choices").isArray() || root.get("choices").size() == 0) {
                throw new RuntimeException("Invalid response format: missing choices");
            }
            
            JsonNode choices = root.get("choices");
            if (!choices.get(0).has("message") || !choices.get(0).get("message").has("content")) {
                throw new RuntimeException("Invalid response format: missing message content");
            }
            
            // 获取 AI 的回复内容
            String aiResponse = choices.get(0).get("message").get("content").asText();
            log.info("Raw AI Response content: {}", aiResponse);
            
            // 尝试修复可能的编码问题
            if (containsGarbledText(aiResponse)) {
                log.warn("Response contains garbled text, attempting to fix encoding issues");
                try {
                    byte[] responseBytes = aiResponse.getBytes(StandardCharsets.UTF_8);
                    aiResponse = new String(responseBytes, StandardCharsets.UTF_8);
                    log.info("Converted response: {}", aiResponse);
                    
                    if (containsGarbledText(aiResponse)) {
                        log.warn("Still contains garbled text after encoding conversion, using default values");
                        return createDefaultResponse();
                    }
                } catch (Exception e) {
                    log.error("Failed to convert encoding: {}", e.getMessage());
                    return createDefaultResponse();
                }
            }
            
            try {
                // 首先尝试解析为JSON格式
                DeepSeekAnalysisResponse result = mapper.readValue(aiResponse, DeepSeekAnalysisResponse.class);
                if (validateResponse(result)) {
                    log.info("Successfully parsed as JSON response: {}", result);
                    return result;
                }
            } catch (Exception e) {
                log.info("Response is not in JSON format, trying to parse as Markdown: {}", e.getMessage());
                // JSON 解析失败，尝试解析为 Markdown 格式
                DeepSeekAnalysisResponse markdownResult = parseMarkdownResponse(aiResponse);
                if (validateResponse(markdownResult)) {
                    log.info("Successfully parsed as Markdown response: {}", markdownResult);
                    return markdownResult;
                } else {
                    log.warn("Failed to parse as Markdown, using default values");
                    return createDefaultResponse();
                }
            }
            
            // 如果解析失败，返回默认值
            return createDefaultResponse();
        } catch (Exception e) {
            log.error("Failed to parse DeepSeek response: {}", e.getMessage(), e);
            return createDefaultResponse();
        }
    }
    
    /**
     * 解析 Markdown 格式的响应
     */
    private DeepSeekAnalysisResponse parseMarkdownResponse(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return createDefaultResponse();
        }
        
        try {
            // 初始化一个默认的响应对象
            DeepSeekAnalysisResponse response = DeepSeekAnalysisResponse.builder()
                .credibilityScore(0.0)
                .factCheckingPoints(new ArrayList<>())
                .misinformationIndicators(new ArrayList<>())
                .verificationRecommendation("")
                .sourceAnalysis(DeepSeekSourceAnalysis.builder()
                    .reliability(0.0)
                    .reputation("未知")
                    .concerns(new ArrayList<>())
                    .build())
                .build();
                
            // 提取可信度评分
            Pattern credibilityPattern = Pattern.compile("可信度评分：(\\d+)%");
            Matcher credibilityMatcher = credibilityPattern.matcher(markdown);
            if (credibilityMatcher.find()) {
                try {
                    int credibility = Integer.parseInt(credibilityMatcher.group(1));
                    response.setCredibilityScore(credibility / 100.0);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse credibility score: {}", e.getMessage());
                }
            }
            
            // 提取事实核查要点
            List<String> factPoints = extractListItems(markdown, "事实核查要点：");
            if (!factPoints.isEmpty()) {
                response.setFactCheckingPoints(factPoints);
            }
            
            // 提取虚假信息指标
            List<String> indicators = extractListItems(markdown, "虚假信息指标：");
            if (!indicators.isEmpty()) {
                response.setMisinformationIndicators(indicators);
            }
            
            // 提取验证建议
            Pattern recommendationPattern = Pattern.compile("验证建议：([^\n]+)");
            Matcher recommendationMatcher = recommendationPattern.matcher(markdown);
            if (recommendationMatcher.find()) {
                response.setVerificationRecommendation(recommendationMatcher.group(1).trim());
            }
            
            // 提取来源分析
            Pattern reliabilityPattern = Pattern.compile("可靠性：(\\d+)%");
            Matcher reliabilityMatcher = reliabilityPattern.matcher(markdown);
            if (reliabilityMatcher.find()) {
                try {
                    int reliability = Integer.parseInt(reliabilityMatcher.group(1));
                    response.getSourceAnalysis().setReliability(reliability / 100.0);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse reliability score: {}", e.getMessage());
                }
            }
            
            Pattern reputationPattern = Pattern.compile("声誉：([^\n]+)");
            Matcher reputationMatcher = reputationPattern.matcher(markdown);
            if (reputationMatcher.find()) {
                response.getSourceAnalysis().setReputation(reputationMatcher.group(1).trim());
            }
            
            // 提取关注点
            List<String> concerns = extractListItems(markdown, "关注点：");
            if (!concerns.isEmpty()) {
                response.getSourceAnalysis().setConcerns(concerns);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Error parsing Markdown response: {}", e.getMessage());
            return createDefaultResponse();
        }
    }
    
    /**
     * 从 Markdown 文本中提取列表项
     */
    private List<String> extractListItems(String markdown, String section) {
        List<String> items = new ArrayList<>();
        
        int sectionIndex = markdown.indexOf(section);
        if (sectionIndex == -1) {
            return items;
        }
        
        // 从section开始到下一个section结束
        int nextSectionIndex = markdown.indexOf("：", sectionIndex + section.length());
        if (nextSectionIndex == -1) {
            nextSectionIndex = markdown.length();
        }
        
        String sectionContent = markdown.substring(sectionIndex + section.length(), nextSectionIndex);
        
        // 提取列表项（以-或*开头）
        Pattern listItemPattern = Pattern.compile("[-*]\\s+([^\\n]+)");
        Matcher matcher = listItemPattern.matcher(sectionContent);
        
        while (matcher.find()) {
            String item = matcher.group(1).trim();
            if (!item.isEmpty() && !containsGarbledText(item)) {
                items.add(item);
            }
        }
        
        return items;
    }
    
    /**
     * 验证响应对象并设置默认值
     */
    private boolean validateResponse(DeepSeekAnalysisResponse response) {
        if (response == null) {
            return false;
        }
        
        try {
            // 设置默认值
            if (response.getCredibilityScore() == null) {
                response.setCredibilityScore(0.0);
            }
            
            if (response.getFactCheckingPoints() == null) {
                response.setFactCheckingPoints(new ArrayList<>());
            } else {
                // 检查事实核查要点是否包含乱码
                List<String> filteredPoints = new ArrayList<>();
                for (String point : response.getFactCheckingPoints()) {
                    if (point != null && !containsGarbledText(point)) {
                        filteredPoints.add(point);
                    }
                }
                response.setFactCheckingPoints(filteredPoints);
            }
            
            if (response.getMisinformationIndicators() == null) {
                response.setMisinformationIndicators(new ArrayList<>());
            } else {
                // 检查虚假信息指标是否包含乱码
                List<String> filteredIndicators = new ArrayList<>();
                for (String indicator : response.getMisinformationIndicators()) {
                    if (indicator != null && !containsGarbledText(indicator)) {
                        filteredIndicators.add(indicator);
                    }
                }
                response.setMisinformationIndicators(filteredIndicators);
            }
            
            if (response.getVerificationRecommendation() == null || 
                response.getVerificationRecommendation().isEmpty() ||
                containsGarbledText(response.getVerificationRecommendation())) {
                response.setVerificationRecommendation("暂无验证建议或验证建议解析失败");
            }
            
            if (response.getSourceAnalysis() == null) {
                response.setSourceAnalysis(DeepSeekSourceAnalysis.builder()
                    .reliability(0.0)
                    .reputation("未知")
                    .concerns(new ArrayList<>())
                    .build());
            } else {
                // 检查 sourceAnalysis 的字段
                if (response.getSourceAnalysis().getReliability() == null) {
                    response.getSourceAnalysis().setReliability(0.0);
                }
                
                if (response.getSourceAnalysis().getReputation() == null || 
                    response.getSourceAnalysis().getReputation().isEmpty() ||
                    containsGarbledText(response.getSourceAnalysis().getReputation())) {
                    response.getSourceAnalysis().setReputation("未知");
                }
                
                if (response.getSourceAnalysis().getConcerns() == null) {
                    response.getSourceAnalysis().setConcerns(new ArrayList<>());
                } else {
                    // 检查关注点是否包含乱码
                    List<String> filteredConcerns = new ArrayList<>();
                    for (String concern : response.getSourceAnalysis().getConcerns()) {
                        if (concern != null && !containsGarbledText(concern)) {
                            filteredConcerns.add(concern);
                        }
                    }
                    response.getSourceAnalysis().setConcerns(filteredConcerns);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error during response validation: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查文本是否包含乱码
     */
    private boolean containsGarbledText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        // 检查常见的中文乱码特征
        boolean hasGarbledChars = text.contains("鏍?") || text.contains("銆?") || 
                                text.contains("鐩?") || text.contains("娓?") ||
                                text.contains("淇?") || text.contains("鍋?");
        
        // 检查不正常的字符分布
        int unicodeCount = 0;
        int totalChars = text.length();
        for (char c : text.toCharArray()) {
            // 检查是否是乱码常见的Unicode范围
            if (c >= 0x800 && c <= 0x9FFF) {
                unicodeCount++;
            }
        }
        
        // 如果超过30%的字符都在这个范围，可能是乱码
        double unicodeRatio = (double) unicodeCount / totalChars;
        boolean hasAbnormalDistribution = unicodeRatio > 0.3;
        
        return hasGarbledChars || hasAbnormalDistribution;
    }
    
    /**
     * 创建默认的分析响应
     */
    private DeepSeekAnalysisResponse createDefaultResponse() {
        DeepSeekSourceAnalysis sourceAnalysis = DeepSeekSourceAnalysis.builder()
            .reliability(0.0)
            .reputation("未知")
            .concerns(Collections.singletonList("由于编码问题，无法正确解析API响应"))
            .build();
            
        return DeepSeekAnalysisResponse.builder()
            .credibilityScore(0.5)
            .factCheckingPoints(Arrays.asList(
                "无法获取详细分析结果，可能是由于编码问题",
                "建议检查API返回的编码设置是否正确"
            ))
            .misinformationIndicators(Collections.singletonList("请检查输入内容是否合适"))
            .verificationRecommendation("由于技术原因，无法提供完整分析。建议尝试其他输入内容或稍后再试。")
            .sourceAnalysis(sourceAnalysis)
            .build();
    }
    
    private List<String> parseList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        }
        return list;
    }
    
    private DeepSeekSourceAnalysis parseSourceAnalysis(JsonNode node) {
        DeepSeekSourceAnalysis sourceAnalysis = new DeepSeekSourceAnalysis();
        if (!node.isMissingNode()) {
            sourceAnalysis.setReliability(node.path("reliability").asDouble(0.0));
            sourceAnalysis.setReputation(node.path("reputation").asText(""));
            sourceAnalysis.setConcerns(parseList(node.path("concerns")));
        }
        return sourceAnalysis;
    }
}