package com.digitalcoin.holdings_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.persistence.EntityManager;

@SpringBootTest
public class SimpleRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testEntityManager() {
        // 简单测试EntityManager是否能正常工作
        System.out.println("EntityManager已成功注入！");
        
        // 直接使用原生SQL查询
        Object result = entityManager.createNativeQuery("SELECT COUNT(*) FROM position WHERE is_current = 1")
                                    .getSingleResult();
        
        System.out.println("当前持仓记录数：" + result);
    }
}