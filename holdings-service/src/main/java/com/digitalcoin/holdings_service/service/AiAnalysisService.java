package com.digitalcoin.holdings_service.service;

public interface AiAnalysisService {
    void analyzeMessage(Long messageId);
    
    /**
     * 生成持仓调整建议并创建报告
     * @param messageId 消息ID
     */
    void generateAdjustSuggest(Long messageId);
}