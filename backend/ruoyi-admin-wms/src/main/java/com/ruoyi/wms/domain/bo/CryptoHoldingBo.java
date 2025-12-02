package com.ruoyi.wms.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 加密货币持有业务对象 crypto_holding
 * 
 * @author system
 * @date 2024-07-19
 */
@Data
public class CryptoHoldingBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Integer id;
    
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
    private String updateTime;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 搜索关键词（用于模糊查询）
     */
    private String keyword;

}
