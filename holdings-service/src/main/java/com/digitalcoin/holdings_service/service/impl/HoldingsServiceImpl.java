package com.digitalcoin.holdings_service.service.impl;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.repository.HoldingsRepository;
import com.digitalcoin.holdings_service.service.HoldingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 加密货币持有服务实现类
 */
@Service
public class HoldingsServiceImpl implements HoldingsService {

    // 显式声明log变量，确保编译通过
    private static final Logger log = LoggerFactory.getLogger(HoldingsServiceImpl.class);

    @Autowired
    private HoldingsRepository holdingsRepository;

    @Override
    public Optional<Holdings> findById(Long id) {
        return holdingsRepository.findById(id);
    }

    @Override
    public Optional<Holdings> findByCoinType(String coinType) {
        return holdingsRepository.findByCoinType(coinType);
    }

    @Override
    @Transactional
    public Holdings save(Holdings holdings) {
        return holdingsRepository.save(holdings);
    }

    @Override
    @Transactional
    public Holdings update(Holdings holdings) {
        return holdingsRepository.save(holdings);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        holdingsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        holdingsRepository.deleteAllById(ids);
    }

    @Override
    public void testDatabaseConnectionFailure() {
        try {
            // 模拟数据库连接失败
            String connectionUrl = "jdbc:mysql://invalid-host:3306/invalid-db";
            java.sql.DriverManager.getConnection(connectionUrl, "invalid-user", "invalid-pass");
        } catch (Exception e) {
            log.error("数据库连接失败测试", e);
            throw new RuntimeException("数据库连接失败", e);
        }
    }

    @Override
    public Page<Holdings> findAll(Pageable pageable) {
        return holdingsRepository.findAll(pageable);
    }
    
    @Override
    public List<Holdings> getAllHoldings() {
        return holdingsRepository.findAll();
    }
    
    @Override
    public List<Holdings> getCurrentHoldings() {
        return holdingsRepository.findByIsCurrent(1);
    }
    
    @Override
    public boolean existsByCoinType(String coinType) {
        return holdingsRepository.existsByCoinType(coinType);
    }
    
    @Transactional
    @Override
    public boolean updateHoldings(List<Holdings> newHoldingsList) {
        try {
            // 更新现有持仓
            for (Holdings newHolding : newHoldingsList) {
                Optional<Holdings> existingHolding = holdingsRepository.findByCoinType(newHolding.getCoinType());
                if (existingHolding.isPresent()) {
                    Holdings holdingToUpdate = existingHolding.get();
                    holdingToUpdate.setAmount(newHolding.getAmount());
                    holdingToUpdate.setPrice(newHolding.getPrice());
                    holdingToUpdate.setTotalValue(newHolding.getTotalValue());
                    holdingsRepository.save(holdingToUpdate);
                } else {
                    holdingsRepository.save(newHolding);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("更新持仓失败", e);
            return false;
        }
    }
    
    @Transactional
    @Override
    public void adjustHoldings(String adjustSuggest) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adjustSuggest);
            JsonNode holdingsNode = rootNode.get("holdings");
            
            if (holdingsNode != null && holdingsNode.isArray()) {
                List<Holdings> newHoldingsList = new ArrayList<>();
                
                for (JsonNode holdingNode : holdingsNode) {
                    Holdings holding = new Holdings();
                    holding.setCoinType(holdingNode.get("coin_type").asText());
                    holding.setAmount(holdingNode.get("amount").asDouble());
                    holding.setPrice(holdingNode.get("price").asDouble());
                    holding.setTotalValue(holdingNode.get("total_value").asDouble());
                    newHoldingsList.add(holding);
                }
                
                // 更新持仓
                updateHoldings(newHoldingsList);
            }
        } catch (Exception e) {
            log.error("调整持仓失败", e);
            throw new RuntimeException("调整持仓失败", e);
        }
    }
}