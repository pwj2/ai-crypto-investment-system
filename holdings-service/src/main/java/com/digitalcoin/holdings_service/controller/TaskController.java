package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.service.AiAnalysisService;
import com.digitalcoin.holdings_service.util.MessageCollectionTask;
import com.digitalcoin.holdings_service.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private MessageCollectionTask messageCollectionTask;
    
    @Autowired
    private AiAnalysisService aiAnalysisService;

    /**
     * 手动触发消息采集任务
     */
    @GetMapping("/collect-messages")
    public String collectMessages() {
        try {
            System.out.println("接收到手动触发消息采集任务的请求");
            messageCollectionTask.collectMessages();
            System.out.println("手动触发消息采集任务成功");
            return "消息采集任务已触发，正在执行中...";
        } catch (Exception e) {
            System.err.println("手动触发消息采集任务失败: " + e.getMessage());
            e.printStackTrace();
            return "触发消息采集任务失败：" + e.getMessage();
        }
    }
    
    /**
     * 手动触发AI分析消息
     */
    @GetMapping("/analyze-message/{messageId}")
    public Result<String> analyzeMessage(@PathVariable Long messageId) {
        try {
            System.out.println("接收到手动触发AI分析消息的请求，消息ID: " + messageId);
            aiAnalysisService.analyzeMessage(messageId);
            System.out.println("手动触发消息分析任务成功，消息ID: " + messageId);
            return Result.success("消息分析已启动，消息ID: " + messageId);
        } catch (Exception e) {
            System.err.println("手动触发消息分析任务失败，消息ID: " + messageId);
            e.printStackTrace();
            return Result.error("分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成持仓调整建议并创建报告
     * @param messageId 消息ID
     * @return 操作结果
     */
    @PostMapping("/generate-adjust-suggest/{messageId}")
    public Result<String> generateAdjustSuggest(@PathVariable Long messageId) {
        try {
            System.out.println("接收到生成持仓调整建议请求，消息ID: " + messageId);
            aiAnalysisService.generateAdjustSuggest(messageId);
            System.out.println("生成调整建议报告成功");
            return Result.success("持仓调整建议已生成，消息ID: " + messageId);
        } catch (Exception e) {
            System.err.println("生成调整建议报告失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("生成失败: " + e.getMessage());
        }
    }
}