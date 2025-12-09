package com.digitalcoin.holdings_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContextTest {

    @Test
    public void testContextLoads() {
        // 只测试Spring上下文能否正常加载
        System.out.println("Spring上下文加载成功！");
    }
}