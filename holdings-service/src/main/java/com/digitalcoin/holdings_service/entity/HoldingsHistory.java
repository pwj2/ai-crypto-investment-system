package com.digitalcoin.holdings_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产变动历史实体类
 */
@Entity
@Table(name = "holdings_history")
public class HoldingsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 资产代码
     */
    @Column(name = "coin_type", nullable = false)
    private String coinType;

    /**
     * 交易类型：BUY-买入，SELL-卖出，TRANSFER-转账
     */
    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    /**
     * 交易数量
     */
    @Column(name = "quantity", nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    /**
     * 交易价格
     */
    @Column(name = "price", precision = 20, scale = 8)
    private BigDecimal price;

    /**
     * 交易金额
     */
    @Column(name = "amount", precision = 20, scale = 8)
    private BigDecimal amount;

    /**
     * 交易前余额
     */
    @Column(name = "before_balance", precision = 20, scale = 8)
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    @Column(name = "after_balance", precision = 20, scale = 8)
    private BigDecimal afterBalance;

    /**
     * 交易时间
     */
    @Column(name = "transaction_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionTime;

    /**
     * 交易描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (transactionTime == null) {
            transactionTime = new Date();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
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

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public BigDecimal getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}