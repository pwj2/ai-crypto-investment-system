package com.digitalcoin.holdings_service.service.impl;

import com.digitalcoin.holdings_service.entity.MessageAnalysisReport;
import com.digitalcoin.holdings_service.repository.MessageAnalysisReportRepository;
import com.digitalcoin.holdings_service.service.MessageAnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 消息分析报告服务实现类
 */
@Service
public class MessageAnalysisReportServiceImpl implements MessageAnalysisReportService {
    
    @Autowired
    private MessageAnalysisReportRepository messageAnalysisReportRepository;
    
    @Override
    @Transactional
    public MessageAnalysisReport save(MessageAnalysisReport report) {
        if (report.getCreateTime() == null) {
            report.setCreateTime(new Date());
        }
        return messageAnalysisReportRepository.save(report);
    }
    
    @Override
    public Optional<MessageAnalysisReport> findById(Long id) {
        return messageAnalysisReportRepository.findById(id);
    }
    
    @Override
    public Optional<MessageAnalysisReport> findByMessageId(Long messageId) {
        return messageAnalysisReportRepository.findByMessageId(messageId);
    }
    
    @Override
    public List<MessageAnalysisReport> findAll() {
        return messageAnalysisReportRepository.findAll();
    }
    
    @Override
    public List<MessageAnalysisReport> findByStatus(String status) {
        return messageAnalysisReportRepository.findByStatusOrderByCreateTimeDesc(status);
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        int updated = messageAnalysisReportRepository.updateStatus(id, status);
        return updated > 0;
    }
    
    @Override
    public boolean existsByMessageId(Long messageId) {
        return messageAnalysisReportRepository.existsByMessageId(messageId);
    }
    
    @Override
    @Transactional
    public MessageAnalysisReport createReportFromMessageAnalysis(Long messageId, String analysisResult, String adjustSuggest) {
        // 检查是否已存在该消息的报告
        if (existsByMessageId(messageId)) {
            // 如果已存在，更新现有报告
            MessageAnalysisReport existingReport = findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("消息分析报告不存在，messageId: " + messageId));
            existingReport.setAnalysisResult(analysisResult);
            existingReport.setAdjustSuggest(adjustSuggest);
            existingReport.setStatus("待审核"); // 重置状态为待审核
            return save(existingReport);
        } else {
            // 创建新报告
            MessageAnalysisReport report = new MessageAnalysisReport();
            report.setMessageId(messageId);
            report.setAnalysisResult(analysisResult);
            report.setAdjustSuggest(adjustSuggest);
            report.setStatus("待审核");
            report.setCreateTime(new Date());
            return save(report);
        }
    }
}