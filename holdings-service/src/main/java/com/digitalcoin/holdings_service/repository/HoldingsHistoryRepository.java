package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.HoldingsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 资产变动历史数据访问接口
 */
@Repository
public interface HoldingsHistoryRepository extends JpaRepository<HoldingsHistory, Long> {

    /**
     * 根据用户ID分页查询资产变动历史
     */
    @Query("SELECT h FROM HoldingsHistory h WHERE h.userId = :userId ORDER BY h.transactionTime DESC")
    Page<HoldingsHistory> findByUserIdOrderByTransactionTimeDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据用户ID和资产代码查询资产变动历史
     */
    @Query("SELECT h FROM HoldingsHistory h WHERE h.userId = :userId AND h.coinType = :coinType ORDER BY h.transactionTime DESC")
    List<HoldingsHistory> findByUserIdAndCoinTypeOrderByTransactionTimeDesc(@Param("userId") Long userId, @Param("coinType") String coinType);

    /**
     * 根据用户ID和交易类型查询资产变动历史
     */
    @Query("SELECT h FROM HoldingsHistory h WHERE h.userId = :userId AND h.transactionType = :transactionType ORDER BY h.transactionTime DESC")
    List<HoldingsHistory> findByUserIdAndTransactionTypeOrderByTransactionTimeDesc(@Param("userId") Long userId, @Param("transactionType") String transactionType);

    /**
     * 查询指定时间范围内的资产变动历史
     */
    @Query("SELECT h FROM HoldingsHistory h WHERE h.userId = :userId AND h.transactionTime BETWEEN :startTime AND :endTime ORDER BY h.transactionTime DESC")
    List<HoldingsHistory> findByUserIdAndTransactionTimeBetween(
            @Param("userId") Long userId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 查询用户特定交易类型的交易总量
     */
    @Query("SELECT SUM(h.amount) FROM HoldingsHistory h WHERE h.userId = :userId AND h.transactionType = :transactionType")
    Double findTotalAmountByUserIdAndTransactionType(@Param("userId") Long userId, @Param("transactionType") String transactionType);
}