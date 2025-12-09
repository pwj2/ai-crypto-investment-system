package com.digitalcoin.holdings_service.repository;

import com.digitalcoin.holdings_service.entity.Holdings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 加密货币持有数据访问接口
 */
@Repository
public interface HoldingsRepository extends JpaRepository<Holdings, Long> {

    /**
     * 根据资产代码查询资产持有信息
     */
    Optional<Holdings> findByCoinType(String coinType);

    /**
     * 查询特定资产的持有总量
     */
    @Query("SELECT SUM(h.amount) FROM Holdings h WHERE h.coinType = :coinType")
    Optional<Double> findTotalAmountByCoinType(@Param("coinType") String coinType);

    /**
     * 检查是否存在特定资产
     */
    boolean existsByCoinType(String coinType);
    
    /**
     * 根据是否为当前持仓查询资产持有信息
     */
    List<Holdings> findByIsCurrent(int isCurrent);

}