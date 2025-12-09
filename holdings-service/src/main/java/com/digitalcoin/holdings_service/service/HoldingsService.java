package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.Holdings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 加密货币持有服务接口
 */
public interface HoldingsService {

    /**
     * 根据ID查询加密货币持有信息
     */
    Optional<Holdings> findById(Long id);

    /**
     * 根据资产代码查询加密货币持有信息
     */
    Optional<Holdings> findByCoinType(String coinType);

    /**
     * 保存加密货币持有信息
     */
    Holdings save(Holdings holdings);

    /**
     * 更新加密货币持有信息
     */
    Holdings update(Holdings holdings);

    /**
     * 删除加密货币持有信息
     */
    void deleteById(Long id);

    /**
     * 批量删除加密货币持有信息
     */
    void deleteByIds(List<Long> ids);

    /**
     * 分页查询加密货币持有信息
     */
    Page<Holdings> findAll(Pageable pageable);
    
    /**
     * 查询当前所有持仓
     */
    List<Holdings> getAllHoldings();
    
    /**
     * 查询当前持仓（is_current=1）
     */
    List<Holdings> getCurrentHoldings();
    
    /**
     * 检查是否存在特定资产
     */
    boolean existsByCoinType(String coinType);
    
    /**
     * 更新持仓
     */
    boolean updateHoldings(List<Holdings> newHoldingsList);
    
    /**
     * 根据调整建议更新持仓
     * @param adjustSuggest 调整建议JSON字符串
     */
    void adjustHoldings(String adjustSuggest);
    
    /**
     * 测试数据库连接失败的异常处理
     * 用于测试全局异常处理机制
     */
    void testDatabaseConnectionFailure();
}