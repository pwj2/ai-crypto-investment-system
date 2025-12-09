package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.SuggestReport;
import com.digitalcoin.holdings_service.service.SuggestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投资建议报告Controller
 */
@RestController
@RequestMapping("/api/suggest-reports")
public class SuggestReportController {

    @Autowired
    private SuggestReportService suggestReportService;

    /**
     * 根据ID查询报告
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuggestReport> getReportById(@PathVariable Long id) {
        return suggestReportService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据用户ID分页查询报告
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SuggestReport>> getReportsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SuggestReport> reportPage = suggestReportService.findByUserId(userId, pageable);
        return ResponseEntity.ok(reportPage);
    }

    /**
     * 根据用户ID和报告类型查询
     */
    @GetMapping("/user/{userId}/type/{reportType}")
    public ResponseEntity<List<SuggestReport>> getReportsByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable String reportType) {
        List<SuggestReport> reports = suggestReportService.findByUserIdAndReportType(userId, reportType);
        return ResponseEntity.ok(reports);
    }

    /**
     * 查询用户未读报告数量
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCountByUserId(@PathVariable Long userId) {
        long unreadCount = suggestReportService.countUnreadByUserId(userId);
        return ResponseEntity.ok(unreadCount);
    }

    /**
     * 创建报告
     */
    @PostMapping
    public ResponseEntity<SuggestReport> createReport(@RequestBody SuggestReport report) {
        SuggestReport saved = suggestReportService.save(report);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 批量创建报告
     */
    @PostMapping("/batch")
    public ResponseEntity<List<SuggestReport>> createReportsBatch(@RequestBody List<SuggestReport> reports) {
        List<SuggestReport> saved = suggestReportService.saveAll(reports);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 删除报告
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        suggestReportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除报告
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteReportsBatch(@RequestBody List<Long> ids) {
        suggestReportService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 标记报告为已读
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        suggestReportService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量标记报告为已读
     */
    @PutMapping("/batch/read")
    public ResponseEntity<Void> markAllAsRead(@RequestBody List<Long> ids) {
        suggestReportService.markAllAsRead(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询用户最新的报告
     */
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<SuggestReport> getLatestReportByUserId(@PathVariable Long userId) {
        return suggestReportService.findLatestByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据日期范围查询报告
     */
    @GetMapping("/user/{userId}/time-range")
    public ResponseEntity<List<SuggestReport>> getReportsByTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<SuggestReport> reports = suggestReportService.findByUserIdAndCreatedTimeBetween(userId, startTime, endTime);
        return ResponseEntity.ok(reports);
    }

    /**
     * 生成投资建议报告
     */
    @PostMapping("/generate")
    public ResponseEntity<SuggestReport> generateReport(
            @RequestParam Long userId,
            @RequestParam String reportType,
            @RequestParam String title,
            @RequestParam String content) {
        SuggestReport report = suggestReportService.createReport(userId, reportType, title, content);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    /**
     * 根据报告状态查询
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<SuggestReport>> getReportsByStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        List<SuggestReport> reports = suggestReportService.findByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(reports);
    }

    /**
     * 更新报告状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        suggestReportService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}