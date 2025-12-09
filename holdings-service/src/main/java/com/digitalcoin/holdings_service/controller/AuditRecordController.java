package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.AuditRecord;
import com.digitalcoin.holdings_service.service.AuditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 审计记录Controller
 */
@RestController
@RequestMapping("/api/audit-records")
public class AuditRecordController {

    @Autowired
    private AuditRecordService auditRecordService;

    /**
     * 根据ID查询审计记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditRecord> getAuditRecordById(@PathVariable Long id) {
        return auditRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据用户ID分页查询审计记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditRecord>> getAuditRecordsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditRecord> recordPage = auditRecordService.findByUserId(userId, pageable);
        return ResponseEntity.ok(recordPage);
    }

    /**
     * 根据操作类型查询审计记录
     */
    @GetMapping("/operation/{operationType}")
    public ResponseEntity<Page<AuditRecord>> getAuditRecordsByOperationType(
            @PathVariable String operationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditRecord> recordPage = auditRecordService.findByOperationType(operationType, pageable);
        return ResponseEntity.ok(recordPage);
    }

    /**
     * 根据模块查询审计记录
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<Page<AuditRecord>> getAuditRecordsByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditRecord> recordPage = auditRecordService.findByModule(module, pageable);
        return ResponseEntity.ok(recordPage);
    }

    /**
     * 根据状态查询审计记录
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AuditRecord>> getAuditRecordsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditRecord> recordPage = auditRecordService.findByStatus(status, pageable);
        return ResponseEntity.ok(recordPage);
    }

    /**
     * 根据日期范围查询审计记录
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<AuditRecord>> getAuditRecordsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<AuditRecord> records = auditRecordService.findByOperationTimeBetween(startTime, endTime);
        return ResponseEntity.ok(records);
    }

    /**
     * 根据用户名和操作类型查询
     */
    @GetMapping("/username/{username}/operation/{operationType}")
    public ResponseEntity<List<AuditRecord>> getByUsernameAndOperationType(
            @PathVariable String username,
            @PathVariable String operationType) {
        List<AuditRecord> records = auditRecordService.findByUsernameAndOperationType(username, operationType);
        return ResponseEntity.ok(records);
    }

    /**
     * 根据IP地址查询
     */
    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<List<AuditRecord>> getByIpAddress(
            @PathVariable String ipAddress) {
        List<AuditRecord> records = auditRecordService.findByIpAddress(ipAddress);
        return ResponseEntity.ok(records);
    }

    /**
     * 保存审计记录
     */
    @PostMapping
    public ResponseEntity<AuditRecord> saveAuditRecord(@RequestBody AuditRecord auditRecord) {
        AuditRecord saved = auditRecordService.save(auditRecord);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 批量保存审计记录
     */
    @PostMapping("/batch")
    public ResponseEntity<List<AuditRecord>> saveAuditRecordsBatch(@RequestBody List<AuditRecord> auditRecords) {
        List<AuditRecord> saved = auditRecordService.saveAll(auditRecords);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 删除审计记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditRecord(@PathVariable Long id) {
        auditRecordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除审计记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteAuditRecordsBatch(@RequestBody List<Long> ids) {
        auditRecordService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 统计用户各类操作数量
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Long>> getUserOperationStatistics(@PathVariable Long userId) {
        Map<String, Long> statistics = auditRecordService.getUserOperationStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 创建审计记录
     */
    @PostMapping("/create")
    public ResponseEntity<AuditRecord> createAuditRecord(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam String operationType,
            @RequestParam String module,
            @RequestParam String status,
            @RequestParam String ipAddress,
            @RequestParam(required = false) String description) {
        AuditRecord savedRecord = auditRecordService.createRecord(userId, username, operationType, module, ipAddress, status, description);
        return ResponseEntity.ok(savedRecord);
    }

    /**
     * 清理过期记录
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Long>> cleanupOldRecords(@RequestParam int days) {
        long deletedCount = auditRecordService.deleteOldRecords(days);
        return ResponseEntity.ok(Collections.singletonMap("deletedCount", deletedCount));
    }

    /**
     * 根据用户ID和操作类型统计次数
     */
    @GetMapping("/user/{userId}/operation/{operationType}/count")
    public ResponseEntity<Long> countByUserIdAndOperationType(
            @PathVariable Long userId,
            @PathVariable String operationType) {
        long count = auditRecordService.countByUserIdAndOperationType(userId, operationType);
        return ResponseEntity.ok(count);
    }

    /**
     * 综合查询（高级搜索）
     */
    @GetMapping("/search")
    public ResponseEntity<Page<AuditRecord>> searchAuditRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "operationTime"));
        Page<AuditRecord> records = auditRecordService.searchAuditRecords(userId, username, operationType, 
                module, status, startTime, endTime, pageable);
        return ResponseEntity.ok(records);
    }
}