package com.digitalcoin.holdings_service.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 消息分析报告实体类 - 关联AI分析结果与持仓调整建议
 */
@Entity
@Table(name = "message_analysis_report")
public class MessageAnalysisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id")  // 关联的消息ID
    private Long messageId;
    
    @Column(name = "analysis_result")  // AI分析结果（情感+影响）
    private String analysisResult;
    
    @Column(name = "adjust_suggest")  // 持仓调整建议（JSON格式）
    private String adjustSuggest;  // 示例：{"BTC":"增加5%","ETH":"减少3%"}
    
    @Column(name = "status")  // 审核状态：待审核/通过/驳回
    private String status = "待审核";
    
    @Column(name = "create_time")
    private Date createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
    
    // 手动添加getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public String getAnalysisResult() {
        return analysisResult;
    }
    
    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
    
    public String getAdjustSuggest() {
        return adjustSuggest;
    }
    
    public void setAdjustSuggest(String adjustSuggest) {
        this.adjustSuggest = adjustSuggest;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}