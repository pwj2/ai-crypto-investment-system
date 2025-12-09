package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.MessageAnalysisReport;
import com.digitalcoin.holdings_service.service.MessageAnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息分析报告Controller
 */
@RestController
@RequestMapping("/api/message-analysis-reports")
public class MessageAnalysisReportController {
    
    @Autowired
    private MessageAnalysisReportService messageAnalysisReportService;
    
    /**
     * 创建或更新消息分析报告
     */
    @PostMapping("/message/{messageId}")
    public ResponseEntity<MessageAnalysisReport> createOrUpdateReport(
            @PathVariable Long messageId,
            @RequestParam String analysisResult,
            @RequestParam String adjustSuggest) {
        try {
            MessageAnalysisReport report = messageAnalysisReportService.createReportFromMessageAnalysis(
                    messageId, analysisResult, adjustSuggest);
            return new ResponseEntity<>(report, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("创建或更新消息分析报告失败：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID查询报告
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageAnalysisReport> getReportById(@PathVariable Long id) {
        return messageAnalysisReportService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据消息ID查询报告
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<MessageAnalysisReport> getReportByMessageId(@PathVariable Long messageId) {
        return messageAnalysisReportService.findByMessageId(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取所有报告
     */
    @GetMapping
    public ResponseEntity<List<MessageAnalysisReport>> getAllReports() {
        List<MessageAnalysisReport> reports = messageAnalysisReportService.findAll();
        return ResponseEntity.ok(reports);
    }
    
    /**
     * 根据状态查询报告
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MessageAnalysisReport>> getReportsByStatus(@PathVariable String status) {
        List<MessageAnalysisReport> reports = messageAnalysisReportService.findByStatus(status);
        return ResponseEntity.ok(reports);
    }
    
    /**
     * 更新报告状态（审核流程）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            // 验证状态值
            if (!"待审核".equals(status) && !"通过".equals(status) && !"驳回".equals(status)) {
                return ResponseEntity.badRequest().body("无效的状态值，只能是：待审核、通过、驳回");
            }
            
            boolean updated = messageAnalysisReportService.updateStatus(id, status);
            if (updated) {
                return ResponseEntity.ok("报告状态更新成功");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("更新报告状态失败：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失败：" + e.getMessage());
        }
    }
}