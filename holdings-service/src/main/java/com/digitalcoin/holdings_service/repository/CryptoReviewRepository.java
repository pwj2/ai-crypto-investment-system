package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.CryptoReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 加密货币评论数据访问接口
 */
@Repository
public interface CryptoReviewRepository extends JpaRepository<CryptoReview, Long> {

    /**
     * 根据评论ID查询评论
     */
    Optional<CryptoReview> findByReviewId(String reviewId);

    /**
     * 根据用户ID查询评论
     */
    List<CryptoReview> findByUserId(String userId);

    /**
     * 根据情绪分析结果查询评论
     */
    List<CryptoReview> findBySentiment(String sentiment);

    /**
     * 根据评分查询评论
     */
    List<CryptoReview> findByScore(Integer score);

    /**
     * 根据评分范围查询评论
     */
    List<CryptoReview> findByScoreBetween(Integer minScore, Integer maxScore);

    /**
     * 根据评论日期范围查询评论
     */
    List<CryptoReview> findByReviewDateBetween(String startDate, String endDate);
}