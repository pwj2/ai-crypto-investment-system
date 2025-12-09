package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.HoldingsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 资产变动历史服务接口
 */
public interface HoldingsHistoryService {

    /**
     * 根据ID查询资产变动历史
     */
    Optional<HoldingsHistory> findById(Long id);

    /**
     * 根据用户ID分页查询资产变动历史
     */
    Page<HoldingsHistory> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和资产代码查询资产变动历史
     */
    List<HoldingsHistory> findByUserIdAndCoinType(Long userId, String coinType);

    /**
     * 根据用户ID和交易类型查询资产变动历史
     */
    List<HoldingsHistory> findByUserIdAndTransactionType(Long userId, String transactionType);

    /**
     * 查询指定时间范围内的资产变动历史
     */
    List<HoldingsHistory> findByUserIdAndTransactionTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 保存资产变动历史
     */
    HoldingsHistory save(HoldingsHistory history);

    /**
     * 批量保存资产变动历史
     */
    List<HoldingsHistory> saveAll(List<HoldingsHistory> histories);

    /**
     * 删除资产变动历史
     */
    void deleteById(Long id);

    /**
     * 批量删除资产变动历史
     */
    void deleteByIds(List<Long> ids);

    /**
     * 查询用户特定交易类型的交易总量
     */
    BigDecimal getTotalAmountByUserIdAndTransactionType(Long userId, String transactionType);

    /**
     * 创建买入记录
     */
    HoldingsHistory createBuyRecord(Long userId, String coinType, BigDecimal quantity, BigDecimal price, String description);

    /**
     * 创建卖出记录
     */
    HoldingsHistory createSellRecord(Long userId, String coinType, BigDecimal quantity, BigDecimal price, String description);

    /**
     * 创建转账记录
     */
    HoldingsHistory createTransferRecord(Long userId, String coinType, BigDecimal quantity, String direction, String description);

    /**
     * 分页查询所有资产变动历史
     */
    Page<HoldingsHistory> findAll(Pageable pageable);

    /**
     * 清理指定时间之前的历史记录
     */
    void cleanupHistoryBefore(LocalDateTime beforeTime);
}