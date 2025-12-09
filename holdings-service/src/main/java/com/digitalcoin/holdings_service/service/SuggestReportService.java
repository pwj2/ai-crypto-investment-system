package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.SuggestReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 投资建议报告服务接口
 */
public interface SuggestReportService {

    /**
     * 根据ID查询报告
     */
    Optional<SuggestReport> findById(Long id);

    /**
     * 根据用户ID分页查询报告
     */
    Page<SuggestReport> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和报告类型查询
     */
    List<SuggestReport> findByUserIdAndReportType(Long userId, String reportType);

    /**
     * 查询用户未读报告数量
     */
    long countUnreadByUserId(Long userId);

    /**
     * 保存报告
     */
    SuggestReport save(SuggestReport report);

    /**
     * 批量保存报告
     */
    List<SuggestReport> saveAll(List<SuggestReport> reports);

    /**
     * 根据ID删除报告
     */
    void deleteById(Long id);

    /**
     * 批量删除报告
     */
    void deleteByIds(List<Long> ids);

    /**
     * 标记报告为已读
     */
    void markAsRead(Long id);

    /**
     * 批量标记报告为已读
     */
    void markAllAsRead(List<Long> ids);

    /**
     * 查询用户最新的报告
     */
    Optional<SuggestReport> findLatestByUserId(Long userId);

    /**
     * 根据日期范围查询报告
     */
    List<SuggestReport> findByUserIdAndCreatedTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 创建投资建议报告
     */
    SuggestReport createReport(Long userId, String reportType, String title, String content);

    /**
     * 根据报告状态查询
     */
    List<SuggestReport> findByUserIdAndStatus(Long userId, String status);

    /**
     * 更新报告状态
     */
    void updateStatus(Long id, String status);
}