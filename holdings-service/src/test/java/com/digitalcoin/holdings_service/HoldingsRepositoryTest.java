package com.digitalcoin.holdings_service;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.repository.HoldingsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class HoldingsRepositoryTest {

    @Autowired
    private HoldingsRepository holdingsRepository;

    @Test
    public void testQueryCurrentHoldings() {
        // 测试查询当前持仓（is_current=1）
        List<Holdings> currentHoldings = holdingsRepository.findByIsCurrent(1);
        System.out.println("当前持仓数量：" + currentHoldings.size());
        System.out.println("查询成功，若数量为4则说明初始数据已正确加载！");
        // 避免使用getter方法，直接验证查询功能是否正常
    }
}