package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.MessageAnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 消息分析报告数据访问接口
 */
@Repository
public interface MessageAnalysisReportRepository extends JpaRepository<MessageAnalysisReport, Long> {
    
    /**
     * 根据消息ID查询分析报告
     */
    Optional<MessageAnalysisReport> findByMessageId(Long messageId);
    
    /**
     * 根据状态查询报告列表
     */
    List<MessageAnalysisReport> findByStatusOrderByCreateTimeDesc(String status);
    
    /**
     * 更新报告状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageAnalysisReport r SET r.status = :status WHERE r.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 查询指定消息ID的报告存在性
     */
    boolean existsByMessageId(Long messageId);
}