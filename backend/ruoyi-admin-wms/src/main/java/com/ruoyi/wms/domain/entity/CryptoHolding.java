package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 加密货币持有对象 crypto_holding
 * 
 * @author system
 * @date 2024-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crypto_holding")
public class CryptoHolding extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    
    /**
     * 币种类型
     */
    private String coinType;
    
    /**
     * 持有数量
     */
    private Double amount;
    
    /**
     * 占比
     */
    private Double percentage;
    
    /**
     * 当前价格
     */
    private Double currentPrice;
    
    /**
     * 总价值
     */
    private Double totalValue;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;

}