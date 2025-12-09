package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.CryptoNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 加密货币新闻数据访问接口
 */
@Repository
public interface CryptoNewsRepository extends JpaRepository<CryptoNews, Long> {

    /**
     * 根据新闻ID查询新闻
     */
    Optional<CryptoNews> findByNewsId(String newsId);

    /**
     * 根据情绪分析结果查询新闻
     */
    List<CryptoNews> findBySentiment(String sentiment);

    /**
     * 根据新闻来源查询新闻
     */
    List<CryptoNews> findBySource(String source);

    /**
     * 根据发布时间范围查询新闻
     */
    List<CryptoNews> findByPublishTimeBetween(String startTime, String endTime);
}