package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.SuggestReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投资建议报告数据访问接口
 */
@Repository
public interface SuggestReportRepository extends JpaRepository<SuggestReport, Long> {

    /**
     * 根据用户ID分页查询报告
     */
    @Query("SELECT r FROM SuggestReport r WHERE r.userId = :userId ORDER BY r.reportDate DESC")
    Page<SuggestReport> findByUserIdOrderByReportDateDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据用户ID和报告类型查询报告
     */
    @Query("SELECT r FROM SuggestReport r WHERE r.userId = :userId AND r.reportType = :reportType ORDER BY r.reportDate DESC")
    List<SuggestReport> findByUserIdAndReportTypeOrderByReportDateDesc(@Param("userId") Long userId, @Param("reportType") String reportType);

    /**
     * 根据用户ID和是否已读状态查询报告
     */
    @Query("SELECT r FROM SuggestReport r WHERE r.userId = :userId AND r.isRead = :isRead ORDER BY r.reportDate DESC")
    List<SuggestReport> findByUserIdAndIsReadOrderByReportDateDesc(@Param("userId") Long userId, @Param("isRead") Boolean isRead);

    /**
     * 查询用户未读报告数量
     */
    @Query("SELECT COUNT(r) FROM SuggestReport r WHERE r.userId = :userId AND r.isRead = false")
    Integer countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 将报告标记为已读
     */
    @Modifying
    @Transactional
    @Query("UPDATE SuggestReport r SET r.isRead = true, r.readAt = :readAt WHERE r.id = :id AND r.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    /**
     * 查询用户最新的报告
     */
    @Query("SELECT r FROM SuggestReport r WHERE r.userId = :userId ORDER BY r.reportDate DESC")
    List<SuggestReport> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据报告日期范围查询报告
     */
    @Query("SELECT r FROM SuggestReport r WHERE r.userId = :userId AND r.reportDate BETWEEN :startDate AND :endDate ORDER BY r.reportDate DESC")
    List<SuggestReport> findByUserIdAndReportDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}