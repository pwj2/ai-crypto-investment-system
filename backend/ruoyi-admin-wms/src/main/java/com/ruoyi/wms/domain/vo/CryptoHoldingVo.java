package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.CryptoHolding;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 加密货币持有视图对象 crypto_holding
 * 
 * @author system
 * @date 2024-07-19
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CryptoHolding.class)
public class CryptoHoldingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;
    
    /**
     * 币种类型
     */
    @ExcelProperty(value = "币种类型")
    private String coinType;
    
    /**
     * 持有数量
     */
    @ExcelProperty(value = "持有数量")
    private Double amount;
    
    /**
     * 占比
     */
    @ExcelProperty(value = "占比")
    private Double percentage;
    
    /**
     * 当前价格
     */
    @ExcelProperty(value = "当前价格")
    private Double currentPrice;
    
    /**
     * 总价值
     */
    @ExcelProperty(value = "总价值")
    private Double totalValue;
    
    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    
    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private String createBy;
    
    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    @ExcelProperty(value = "更新者")
    private String updateBy;
    
    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}