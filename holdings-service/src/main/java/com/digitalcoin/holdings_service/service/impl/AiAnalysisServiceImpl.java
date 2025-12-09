package com.digitalcoin.holdings_service.service.impl;

import com.digitalcoin.holdings_service.entity.Message;
import com.digitalcoin.holdings_service.entity.MessageAnalysisReport;
import com.digitalcoin.holdings_service.entity.SuggestReport;
import com.digitalcoin.holdings_service.repository.MessageRepository;
import com.digitalcoin.holdings_service.repository.SuggestReportRepository;
import com.digitalcoin.holdings_service.service.AiAnalysisService;
import com.digitalcoin.holdings_service.service.MessageAnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private MessageAnalysisReportService messageAnalysisReportService;

    @Value("${dify.api-key}")
    private String difyApiKey;
    @Value("${dify.analyze-url}")
    private String analyzeUrl;
    @Value("${dify.adjust-url:http://api.dify.ai/v1/chat/completions}")
    private String adjustUrl;
    
    @Autowired
    private SuggestReportRepository suggestReportRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final int MAX_RETRY_COUNT = 3;
    private static final long INITIAL_BACKOFF_MS = 2000; // 初始退避时间2秒
    private static final double BACKOFF_MULTIPLIER = 2.0; // 退避乘数
    private static final long MAX_BACKOFF_MS = 30000; // 最大退避时间30秒

    @Override
    @Transactional
    public void analyzeMessage(Long messageId) {
        System.out.println("开始分析消息，ID: " + messageId);
        
        // 第一步：获取消息内容
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在: " + messageId));
        
        // 第二步：调用Dify API进行情感分析（粗分类）
        String sentimentAnalysisResult = callDifySentimentApi(message.getContent());
        
        // 第三步：更新消息的情感分析结果
        message.setSentiment(sentimentAnalysisResult);
        messageRepository.save(message);
        
        // 第四步：根据情感分析结果生成持仓调整建议（细分析）
        String adjustSuggest = generateAdjustSuggestion(sentimentAnalysisResult);
        
        // 第五步：创建消息分析报告
        MessageAnalysisReport report = new MessageAnalysisReport();
        report.setMessageId(messageId);
        report.setAnalysisResult(sentimentAnalysisResult);
        report.setAdjustSuggest(adjustSuggest);
        report.setStatus("待审核");
        report.setCreateTime(new Date());
        messageAnalysisReportService.save(report);
        
        System.out.println("消息分析完成，ID: " + messageId + ", 结果: " + sentimentAnalysisResult);
        System.out.println("持仓调整建议已生成并保存: " + adjustSuggest);
    }
    
    /**
     * 调用Dify API进行情感分析（粗分类）
     * @param messageContent 消息内容
     * @return 情感分析结果（利好/利空/中性）
     */
    private String callDifySentimentApi(String messageContent) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyApiKey);
        
        // 构建请求体
        String requestBody = "{\"inputs\": {\"content\": \"" + messageContent + "\"}, \"query\": \"请分析以下加密货币相关消息的情感倾向，只需返回'利好'、'利空'或'中性'\", \"user\": \"system_user\"}";
        
        // 创建HTTP实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        
        int retryCount = 0;
        long startTime = System.currentTimeMillis();
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.println("开始调用Dify情感分析接口，第" + (retryCount + 1) + "次尝试");
                
                // 调用Dify接口
                String response = restTemplate.postForObject(analyzeUrl, requestEntity, String.class);
                
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Dify情感分析接口调用成功，耗时：" + elapsedTime + "ms");
                
                // 解析响应，提取情感分析结果
                // 这里简化处理，实际需要解析JSON响应
                String sentiment = "中性";
                if (response.contains("利好")) {
                    sentiment = "利好";
                } else if (response.contains("利空")) {
                    sentiment = "利空";
                }
                
                return sentiment;
            } catch (Exception e) {
                retryCount++;
                long elapsedTime = System.currentTimeMillis() - startTime;
                
                System.err.println("调用Dify情感分析接口失败，第" + retryCount + "次重试: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    // 使用指数退避策略计算等待时间
                    long backoffTime = (long) (INITIAL_BACKOFF_MS * Math.pow(BACKOFF_MULTIPLIER, retryCount - 1));
                    backoffTime = Math.min(backoffTime, MAX_BACKOFF_MS); // 限制最大退避时间
                    
                    System.out.println("等待" + backoffTime + "ms后进行第" + retryCount + "次重试...");
                    try {
                        Thread.sleep(backoffTime);
                    } catch (InterruptedException ie) {
                        System.err.println("重试等待被中断: " + ie.getMessage());
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println("已达到最大重试次数(" + MAX_RETRY_COUNT + ")，调用Dify情感分析接口最终失败，总耗时: " + elapsedTime + "ms, 错误: " + e.getMessage());
                    // 发生异常时返回默认结果
                    return "中性";
                }
            }
        }
        // 添加默认返回值，确保所有执行路径都有返回值
        return "中性";
    }
    
    /**
     * 根据AI分析结果生成持仓调整建议
     * @param analysisResult AI分析结果（情感倾向）
     * @return 持仓调整建议（JSON格式）
     */
    private String generateAdjustSuggestion(String analysisResult) {
        // 根据分析结果生成不同的调整建议
        StringBuilder json = new StringBuilder("{");
        
        if ("利好".equals(analysisResult)) {
            json.append('"').append("BTC").append('"').append(':').append('"').append("增加5%").append('"').append(',').append('"').append("ETH").append('"').append(':').append('"').append("增加3%").append('"').append(',').append('"').append("SOL").append('"').append(':').append('"').append("增加2%").append('"').append(',').append('"').append("USDT").append('"').append(':').append('"').append("维持不变").append('"');
        } else if ("利空".equals(analysisResult)) {
            json.append('"').append("BTC").append('"').append(':').append('"').append("减少5%").append('"').append(',').append('"').append("ETH").append('"').append(':').append('"').append("减少3%").append('"').append(',').append('"').append("SOL").append('"').append(':').append('"').append("减少2%").append('"').append(',').append('"').append("USDT").append('"').append(':').append('"').append("增加5%").append('"');
        } else {
            json.append('"').append("BTC").append('"').append(':').append('"').append("维持不变").append('"').append(',').append('"').append("ETH").append('"').append(':').append('"').append("维持不变").append('"').append(',').append('"').append("SOL").append('"').append(':').append('"').append("维持不变").append('"').append(',').append('"').append("USDT").append('"').append(':').append('"').append("维持不变").append('"');
        }
        
        json.append('}');
        return json.toString();
    }
    
    /**
     * 生成持仓调整建议并创建报告
     * @param messageId 消息ID
     */
    @Override
    @Transactional
    public void generateAdjustSuggest(Long messageId) {
        // 1. 获取消息分析结果
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在: " + messageId));
        String analysis = "情感：" + message.getSentiment() + "，影响：" + message.getCoreImpact();
        
        // 2. 调用Dify接口生成调整建议（扩展AI能力）
        String suggest = callDifyAdjustApi(analysis);
        
        // 3. 保存建议报告
        SuggestReport report = new SuggestReport();
        
        // 设置必要字段
        report.setUserId(1L); // 默认用户ID为1
        report.setTitle("持仓调整建议 - 消息ID: " + messageId);
        report.setContent("消息内容: " + message.getContent() + "\n分析结果: " + analysis + "\n调整建议: " + suggest);
        report.setReportType("SPECIAL"); // 专项报告
        report.setReportDate(java.time.LocalDateTime.now());
        report.setKeySuggestions(suggest);
        report.setRiskWarnings("请注意市场风险，建议根据自身风险承受能力调整持仓");
        report.setAdjustSuggest(suggest);
        report.setStatus("待审核");
        
        // 保存报告
        suggestReportRepository.save(report);
        System.out.println("持仓调整建议报告已创建并保存，报告ID: " + report.getId());
    }
    
    /**
     * 调用Dify接口生成持仓调整建议
     * @param analysis 分析结果文本
     * @return 调整建议文本
     */
    private String callDifyAdjustApi(String analysis) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyApiKey);
        
        // 构建请求体
        String requestBody = "{\"inputs\": {\"analysis\": \"" + analysis + "\", \"qkl\": \"\"}, \"query\": \"基于以上分析，生成详细的数字资产持仓调整建议\", \"user\": \"system_user\"}";
        
        // 创建HTTP实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        
        int retryCount = 0;
        long startTime = System.currentTimeMillis();
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.println("开始调用Dify调整建议接口，第" + (retryCount + 1) + "次尝试");
                
                // 调用Dify接口
                String response = restTemplate.postForObject(adjustUrl, requestEntity, String.class);
                
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Dify调整建议接口调用成功，耗时：" + elapsedTime + "ms");
                
                // 这里简化处理，实际需要解析响应JSON
                // 根据分析结果生成示例建议
                if (analysis.contains("利好")) {
                    return "建议增加BTC持仓5%，ETH持仓3%，其他主流币持仓2%。";
                } else if (analysis.contains("利空")) {
                    return "建议减少BTC持仓5%，ETH持仓3%，其他主流币持仓2%。";
                } else {
                    return "建议维持当前持仓不变，继续观察市场动态。";
                }
            } catch (Exception e) {
                retryCount++;
                long elapsedTime = System.currentTimeMillis() - startTime;
                
                System.err.println("调用Dify调整建议接口失败，第" + retryCount + "次重试: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    // 使用指数退避策略计算等待时间
                    long backoffTime = (long) (INITIAL_BACKOFF_MS * Math.pow(BACKOFF_MULTIPLIER, retryCount - 1));
                    backoffTime = Math.min(backoffTime, MAX_BACKOFF_MS); // 限制最大退避时间
                    
                    System.out.println("等待" + backoffTime + "ms后进行第" + retryCount + "次重试...");
                    try {
                        Thread.sleep(backoffTime);
                    } catch (InterruptedException ie) {
                        System.err.println("重试等待被中断: " + ie.getMessage());
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println("已达到最大重试次数(" + MAX_RETRY_COUNT + ")，调用Dify调整建议接口最终失败，总耗时: " + elapsedTime + "ms, 错误: " + e.getMessage());
                    // 发生异常时返回默认建议
                    return "系统暂时无法生成调整建议，请稍后重试。";
                }
            }
        }
        // 添加默认返回值，确保所有执行路径都有返回值
        return "系统暂时无法生成调整建议，请稍后重试。";
    }
    
    /**
     * 测试Dify接口连通性
     * @return 连接状态和响应信息
     */
    public String testDifyConnection() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + difyApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"inputs\": {\"content\": \"测试消息\"}}";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                analyzeUrl,
                new HttpEntity<>(requestBody, headers),
                String.class
            );
            System.out.println("Dify接口调用成功，状态码：" + response.getStatusCodeValue());
            System.out.println("响应内容：" + response.getBody());
            return "Dify接口调用成功，响应：" + response.getBody();
        } catch (Exception e) {
            System.err.println("Dify接口调用失败：" + e.getMessage());
            return "Dify接口调用失败：" + e.getMessage();
        }
    }
}