package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.MessageAnalysisReport;
import java.util.List;
import java.util.Optional;

/**
 * 消息分析报告服务接口
 */
public interface MessageAnalysisReportService {
    
    /**
     * 保存分析报告
     */
    MessageAnalysisReport save(MessageAnalysisReport report);
    
    /**
     * 根据ID查询报告
     */
    Optional<MessageAnalysisReport> findById(Long id);
    
    /**
     * 根据消息ID查询报告
     */
    Optional<MessageAnalysisReport> findByMessageId(Long messageId);
    
    /**
     * 获取所有报告
     */
    List<MessageAnalysisReport> findAll();
    
    /**
     * 根据状态查询报告列表
     */
    List<MessageAnalysisReport> findByStatus(String status);
    
    /**
     * 更新报告状态
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 检查是否已存在指定消息的报告
     */
    boolean existsByMessageId(Long messageId);
    
    /**
     * 根据消息分析结果创建持仓调整建议报告
     */
    MessageAnalysisReport createReportFromMessageAnalysis(Long messageId, String analysisResult, String adjustSuggest);
}