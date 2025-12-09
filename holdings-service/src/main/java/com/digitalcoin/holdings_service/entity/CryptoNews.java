package com.digitalcoin.holdings_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crypto_news")  // 对应数据库表名
public class CryptoNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
    private Long id;

    @Column(name = "news_id", nullable = false)
    private String newsId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "news_text")
    private String content;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "publish_time", nullable = false)
    private String publishTime;

    @Column(name = "sentiment", nullable = false)
    private String sentiment;

    @Column(name = "create_time", nullable = false)
    private String createTime;

    @Column(name = "update_time", nullable = false)
    private String updateTime;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}