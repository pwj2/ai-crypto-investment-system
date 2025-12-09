package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.repository.HoldingsRepository;
import com.digitalcoin.holdings_service.service.HoldingsService;
import com.digitalcoin.holdings_service.service.impl.AiAnalysisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private HoldingsRepository holdingsRepository;
    
    @Autowired
    private HoldingsService holdingsService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AiAnalysisServiceImpl aiAnalysisService;

    @GetMapping("/holdings")
    public String testHoldings() {
        // 查询当前持仓
        List<Holdings> currentHoldings = holdingsRepository.findByIsCurrent(1);
        return "当前持仓数量：" + currentHoldings.size() + "\n查询成功！";
    }
    
    /**
     * 测试数据库连接失败的异常处理
     * 用于测试全局异常处理器是否正常工作
     */
    @GetMapping("/db-error")
    public String testDatabaseError() {
        System.out.println("调用数据库错误测试接口");
        // 调用服务层方法，该方法会抛出数据库连接异常
        holdingsService.testDatabaseConnectionFailure();
        return "不应该到达这里";
    }
    
    /**
     * 测试数据库连接和核心表读写
     * 验证position表和message表的数据访问
     */
    @GetMapping("/db-test")
    public String testDbConnection() {
        try {
            // 测试持仓表查询
            Integer positionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM position", Integer.class);
            // 测试消息表查询
            Integer messageCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM message", Integer.class);
            return String.format("数据库连接正常！持仓表数据量：%d，消息表数据量：%d",
                positionCount, messageCount);
        } catch (Exception e) {
            return "数据库查询失败：" + e.getMessage();
        }
    }
    
    @GetMapping("/dify-test")
    public String testDifyConnection() {
        return aiAnalysisService.testDifyConnection();
    }
}