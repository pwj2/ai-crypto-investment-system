package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.AuditRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 审计记录服务接口
 */
public interface AuditRecordService {

    /**
     * 根据ID查询审计记录
     */
    Optional<AuditRecord> findById(Long id);

    /**
     * 根据用户ID分页查询审计记录
     */
    Page<AuditRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据操作类型分页查询审计记录
     */
    Page<AuditRecord> findByOperationType(String operationType, Pageable pageable);

    /**
     * 根据模块分页查询审计记录
     */
    Page<AuditRecord> findByModule(String module, Pageable pageable);

    /**
     * 根据状态分页查询审计记录
     */
    Page<AuditRecord> findByStatus(String status, Pageable pageable);

    /**
     * 根据时间范围查询审计记录
     */
    List<AuditRecord> findByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户名和操作类型查询审计记录
     */
    List<AuditRecord> findByUsernameAndOperationType(String username, String operationType);

    /**
     * 根据IP地址查询审计记录
     */
    List<AuditRecord> findByIpAddress(String ipAddress);

    /**
     * 保存审计记录
     */
    AuditRecord save(AuditRecord record);

    /**
     * 批量保存审计记录
     */
    List<AuditRecord> saveAll(List<AuditRecord> records);

    /**
     * 根据ID删除审计记录
     */
    void deleteById(Long id);

    /**
     * 批量删除审计记录
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计操作数量
     */
    long countByOperationTypeAndOperationTimeBetween(String operationType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 创建审计记录
     */
    AuditRecord createRecord(Long userId, String username, String operationType, String module, 
                           String ipAddress, String status, String description);

    /**
     * 清理过期的审计记录
     */
    void cleanupOldRecords(LocalDateTime beforeTime);
    
    /**
     * 删除指定天数之前的旧记录并返回删除的记录数
     */
    long deleteOldRecords(int days);

    /**
     * 根据用户ID和操作类型统计操作次数
     */
    
    /**
     * 综合搜索审计记录
     */
    Page<AuditRecord> searchAuditRecords(Long userId, String username, String operationType,
                                        String module, String status, LocalDateTime startTime,
                                        LocalDateTime endTime, Pageable pageable);
    long countByUserIdAndOperationType(Long userId, String operationType);
    
    /**
     * 统计用户各类操作数量
     */
    Map<String, Long> getUserOperationStatistics(Long userId);
}