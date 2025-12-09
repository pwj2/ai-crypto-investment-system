package com.digitalcoin.holdings_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 根路径控制器
 */
@RestController
public class RootController {

    /**
     * 根路径API，返回基本信息
     */
    @GetMapping("/")
    public Map<String, Object> getRootInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Holdings Service");
        info.put("version", "1.0.0");
        info.put("description", "数字资产持仓管理服务");
        info.put("status", "running");
        info.put("swagger-ui", "/swagger-ui.html");
        info.put("message", "欢迎使用数字资产持仓管理服务！请访问 /swagger-ui.html 查看API文档。");
        return info;
    }
}