package com.digitalcoin.holdings_service.entity;
 
 import jakarta.persistence.*;
 import java.util.Date;

 @Entity
 @Table(name = "crypto_holding")  // 对应数据库表名
 public class Holdings {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
     private Long id;

     @Column(name = "coin_type", nullable = false)  // 对应表中coin_type字段
     private String coinType;  // 数字货币类型（BTC/ETH等）

     @Column(name = "amount", nullable = false)
     private Double amount;  // 资产数量

     @Column(name = "price", nullable = false)
     private Double price;  // 当前价格

     @Column(name = "total_value", nullable = false)
     private Double totalValue;  // 总资产价值

     @Column(name = "create_time", nullable = false, updatable = false)
     private String createTime;  // 创建时间（不允许更新）

     @Column(name = "update_time", nullable = false)
     private String updateTime;  // 更新时间
      
     @Column(name = "is_current", nullable = false, columnDefinition = "int default 1")
     private int isCurrent;  // 是否为当前持仓（1:是，0:否）

     // 自动填充创建/更新时间（可选但推荐）
     @PrePersist  // 插入时执行
     public void prePersist() {
         this.createTime = new Date().toString();
         this.updateTime = new Date().toString();
     }

     @PreUpdate  // 更新时执行
     public void preUpdate() {
         this.updateTime = new Date().toString();
     }

     // 手动实现getter和setter方法
     public Long getId() {
         return id;
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getCoinType() {
         return coinType;
     }

     public void setCoinType(String coinType) {
         this.coinType = coinType;
     }

     public Double getAmount() {
         return amount;
     }

     public void setAmount(Double amount) {
         this.amount = amount;
     }

     public Double getPrice() {
         return price;
     }

     public void setPrice(Double price) {
         this.price = price;
     }

     public Double getTotalValue() {
         return totalValue;
     }

     public void setTotalValue(Double totalValue) {
         this.totalValue = totalValue;
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

     public int getIsCurrent() {
         return isCurrent;
     }

     public void setIsCurrent(int isCurrent) {
         this.isCurrent = isCurrent;
     }
}