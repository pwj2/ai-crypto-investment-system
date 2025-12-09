package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.SuggestReport;
import com.digitalcoin.holdings_service.service.HoldingsService;
import com.digitalcoin.holdings_service.service.SuggestReportService;
import com.digitalcoin.holdings_service.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 审核控制器，处理建议报告的审核操作
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private SuggestReportService reportService;
    
    @Autowired
    private HoldingsService holdingsService;
    
    /**
     * 审核通过
     * @param reportId 报告ID
     * @return 操作结果
     */
    @PostMapping("/pass/{reportId}")
    public Result<String> passReview(@PathVariable Long reportId) {
        System.out.println("收到审核通过请求，报告ID: " + reportId);
        
        // 获取报告
        Optional<SuggestReport> optionalReport = reportService.findById(reportId);
        if (optionalReport.isEmpty()) {
            return Result.error("报告不存在");
        }
        
        SuggestReport report = optionalReport.get();
        
        // 更新报告状态为通过
        report.setStatus("APPROVED");
        reportService.save(report);
        System.out.println("更新报告状态为通过");
        
        // 执行持仓调整操作
        if (report.getAdjustSuggest() != null && !report.getAdjustSuggest().isEmpty()) {
            System.out.println("执行持仓调整操作: " + report.getAdjustSuggest());
            try {
                holdingsService.adjustHoldings(report.getAdjustSuggest());
                System.out.println("持仓调整操作执行成功");
            } catch (Exception e) {
                System.out.println("持仓调整操作执行失败: " + e.getMessage());
                return Result.error("审核通过，但持仓调整失败: " + e.getMessage());
            }
        }
        
        // 生成审计日志
        System.out.println("生成审计日志: 审核通过操作，报告ID: " + reportId);
        
        return Result.success("审核通过，持仓已更新");
    }
    
    /**
     * 审核驳回
     * @param reportId 报告ID
     * @param reason 驳回原因
     * @return 操作结果
     */
    @PostMapping("/reject/{reportId}")
    public Result<String> rejectReview(@PathVariable Long reportId, @RequestParam String reason) {
        System.out.println("收到审核驳回请求，报告ID: " + reportId);
        
        // 获取报告
        Optional<SuggestReport> optionalReport = reportService.findById(reportId);
        if (optionalReport.isEmpty()) {
            return Result.error("报告不存在");
        }
        
        SuggestReport report = optionalReport.get();
        
        // 更新报告状态为驳回
        report.setStatus("REJECTED");
        report.setRejectReason(reason);
        reportService.save(report);
        
        System.out.println("更新报告状态为驳回");
        System.out.println("驳回原因: " + reason);
        
        // 生成审计日志
        System.out.println("生成审计日志: 审核驳回操作，报告ID: " + reportId + ", 原因: " + reason);
        
        return Result.success("审核驳回");
    }
}