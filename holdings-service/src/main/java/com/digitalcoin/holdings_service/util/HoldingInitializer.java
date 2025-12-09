package com.digitalcoin.holdings_service.util;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.service.HoldingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 加密货币默认持仓初始化类
 * 在应用启动时自动创建默认持仓
 */
@Component
public class HoldingInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HoldingInitializer.class);

    @Autowired
    private HoldingsService holdingsService;

    // 默认持仓配置
    private static final List<HoldingConfig> DEFAULT_HOLDINGS = List.of(
            new HoldingConfig("BTC", 0.5, 45000.0, "比特币"),
            new HoldingConfig("ETH", 2.0, 3000.0, "以太坊"),
            new HoldingConfig("SOL", 10.0, 120.0, "Solana"),
            new HoldingConfig("USDT", 10000.0, 1.0, "泰达币")
    );

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化默认加密货币持仓...");
        
        // 检查是否已有持仓数据
        List<Holdings> existingHoldings = holdingsService.getAllHoldings();
        
        if (existingHoldings.isEmpty()) {
            log.info("未发现现有持仓，开始创建默认持仓");
            createDefaultHoldings();
        } else {
            log.info("已发现现有持仓数据，跳过默认持仓初始化");
            log.info("当前持仓数量: {}", existingHoldings.size());
        }
    }

    /**
     * 创建默认持仓
     */
    private void createDefaultHoldings() {
        List<Holdings> holdingsToSave = new ArrayList<>();
        
        for (HoldingConfig config : DEFAULT_HOLDINGS) {
            Holdings holding = new Holdings();
            holding.setCoinType(config.coinType);
            holding.setAmount(config.amount);
            holding.setPrice(config.price);
            holding.setTotalValue(config.amount * config.price);
            holding.setIsCurrent(1); // 设置为当前持仓
            
            holdingsToSave.add(holding);
        }
        
        // 保存默认持仓
        for (Holdings holding : holdingsToSave) {
            holdingsService.save(holding);
            log.info("已创建默认持仓: {} - {} 个，总价值: ${}", 
                    holding.getCoinType(), holding.getAmount(), holding.getTotalValue());
        }
        
        log.info("默认持仓初始化完成，共创建 {} 种加密货币持仓", holdingsToSave.size());
    }

    /**
     * 默认持仓配置内部类
     */
    private static class HoldingConfig {
        private final String coinType;
        private final double amount;
        private final double price;
        private final String description;

        public HoldingConfig(String coinType, double amount, double price, String description) {
            this.coinType = coinType;
            this.amount = amount;
            this.price = price;
            this.description = description;
        }
    }
}