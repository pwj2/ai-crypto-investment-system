package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.AuditRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 审计记录数据访问接口
 */
@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    /**
     * 根据用户ID分页查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.userId = :userId ORDER BY a.operationTime DESC")
    Page<AuditRecord> findByUserIdOrderByOperationTimeDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据操作类型分页查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.operationType = :operationType ORDER BY a.operationTime DESC")
    Page<AuditRecord> findByOperationTypeOrderByOperationTimeDesc(@Param("operationType") String operationType, Pageable pageable);

    /**
     * 根据模块分页查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.module = :module ORDER BY a.operationTime DESC")
    Page<AuditRecord> findByModuleOrderByOperationTimeDesc(@Param("module") String module, Pageable pageable);

    /**
     * 根据操作状态分页查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.status = :status ORDER BY a.operationTime DESC")
    Page<AuditRecord> findByStatusOrderByOperationTimeDesc(@Param("status") String status, Pageable pageable);

    /**
     * 根据操作时间范围查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.operationTime BETWEEN :startTime AND :endTime ORDER BY a.operationTime DESC")
    List<AuditRecord> findByOperationTimeBetween(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 根据用户名和操作类型查询审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.username = :username AND a.operationType = :operationType ORDER BY a.operationTime DESC")
    List<AuditRecord> findByUsernameAndOperationTypeOrderByOperationTimeDesc(@Param("username") String username, @Param("operationType") String operationType);

    /**
     * 查询指定IP地址的审计记录
     */
    @Query("SELECT a FROM AuditRecord a WHERE a.ipAddress = :ipAddress ORDER BY a.operationTime DESC")
    List<AuditRecord> findByIpAddressOrderByOperationTimeDesc(@Param("ipAddress") String ipAddress);

    /**
     * 统计指定时间范围内的操作数量
     */
    @Query("SELECT COUNT(a) FROM AuditRecord a WHERE a.operationTime BETWEEN :startTime AND :endTime AND a.operationType = :operationType")
    Long countByOperationTimeBetweenAndOperationType(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("operationType") String operationType);
}