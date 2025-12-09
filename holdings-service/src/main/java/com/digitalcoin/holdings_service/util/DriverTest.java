package com.digitalcoin.holdings_service.util;

/**
 * 简单的MySQL驱动加载测试类
 */
public class DriverTest {
    public static void main(String[] args) {
        try {
            // 尝试加载MySQL驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功！");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}