package com.digitalcoin.holdings_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 投资建议报告实体类
 */
@Entity
@Table(name = "t_suggest_report")
public class SuggestReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 报告标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 报告内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 报告类型：DAILY-日报，WEEKLY-周报，MONTHLY-月报，SPECIAL-专项报告
     */
    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType;

    /**
     * 生成日期
     */
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    /**
     * 重点建议
     */
    @Column(name = "key_suggestions", columnDefinition = "TEXT")
    private String keySuggestions;

    /**
     * 风险提示
     */
    @Column(name = "risk_warnings", columnDefinition = "TEXT")
    private String riskWarnings;

    /**
     * 是否已读：0-未读，1-已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 阅读时间
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * 调整建议JSON
     */
    @Column(name = "adjust_suggest", columnDefinition = "TEXT")
    private String adjustSuggest;
    
    /**
     * 审核状态：待审核、通过、驳回
     */
    @Column(name = "status", length = 20)
    private String status = "待审核";
    
    /**
     * 驳回原因
     */
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (reportDate == null) {
            reportDate = LocalDateTime.now();
        }
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getKeySuggestions() {
        return keySuggestions;
    }

    public void setKeySuggestions(String keySuggestions) {
        this.keySuggestions = keySuggestions;
    }

    public String getRiskWarnings() {
        return riskWarnings;
    }

    public void setRiskWarnings(String riskWarnings) {
        this.riskWarnings = riskWarnings;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
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

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}