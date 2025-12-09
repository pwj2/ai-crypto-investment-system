package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 加密货币消息数据访问接口
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 根据加密货币类型分页查询消息
     */
    @Query("SELECT m FROM Message m WHERE m.coinType = :coinType ORDER BY m.createTime DESC")
    Page<Message> findByCoinTypeOrderByCreatedAtDesc(@Param("coinType") String coinType, Pageable pageable);

    /**
     * 根据加密货币类型和状态查询消息
     */
    @Query("SELECT m FROM Message m WHERE m.coinType = :coinType AND m.isNew = :isNew ORDER BY m.createTime DESC")
    List<Message> findByCoinTypeAndIsNewOrderByCreatedAtDesc(@Param("coinType") String coinType, @Param("isNew") Integer isNew);

    /**
     * 根据情感倾向查询消息
     */
    @Query("SELECT m FROM Message m WHERE m.sentiment = :sentiment ORDER BY m.createTime DESC")
    List<Message> findBySentimentOrderByCreatedAtDesc(@Param("sentiment") String sentiment);

    /**
     * 查询特定加密货币的最新消息（仅返回未读消息）
     */
    @Query("SELECT m FROM Message m WHERE m.coinType = :coinType AND m.isNew = 1 ORDER BY m.createTime DESC")
    List<Message> findLatestByCoinType(@Param("coinType") String coinType, Pageable pageable);

    /**
     * 将消息标记为已处理
     */
    @Modifying
    @Transactional
    // 对于更新操作，保留@Query注解
    @Query("UPDATE Message m SET m.isNew = 0 WHERE m.id = :id")
    int markAsProcessed(@Param("id") Long id);

    /**
     * 查询指定时间范围内的消息
     */
    @Query("SELECT m FROM Message m WHERE m.createTime BETWEEN :startTime AND :endTime ORDER BY m.createTime DESC")
    List<Message> findByCreatedAtBetween(
            @Param("startTime") Date startTime, 
            @Param("endTime") Date endTime);

    /**
     * 删除指定时间之前的消息
     */
    @Modifying
    @Transactional
    // 对于删除操作，保留@Query注解
    @Query("DELETE FROM Message m WHERE m.createTime < :beforeTime")
    int deleteByCreatedAtBefore(@Param("beforeTime") Date beforeTime);
    
    /**
     * 根据内容和加密货币类型查询相似消息（用于去重）
     */
    List<Message> findByContentContainingIgnoreCaseAndCoinType(String content, String coinType);
}