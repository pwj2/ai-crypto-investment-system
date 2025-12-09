package com.digitalcoin.holdings_service.entity;
 
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")  // 对应数据库表名
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coin_type", nullable = false)
    private String coinType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;  // 消息内容（长文本）

    @Column(name = "source")
    private String source;  // 消息来源

    @Column(name = "source_url")
    private String sourceUrl;  // 来源链接

    @Column(name = "sentiment", nullable = false)
    private String sentiment;  // 情感倾向（利好/利空/中性）

    @Column(name = "core_impact")
    private String coreImpact;  // 核心影响点

    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "is_new")
    private Integer isNew;  // 是否未读（1-未读，0-已读）

    @PrePersist
    public void prePersist() {
        this.createTime = new Date();
    }
    
    // 手动添加getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    
    public String getCoreImpact() {
        return coreImpact;
    }
    
    public void setCoreImpact(String coreImpact) {
        this.coreImpact = coreImpact;
    }
    
    public String getCoinType() {
        return coinType;
    }
    
    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getIsNew() {
        return isNew;
    }
    
    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }
}