package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.domain.bo.CryptoHoldingBo;
import com.ruoyi.wms.domain.vo.CryptoHoldingVo;
import java.util.Collection;
import java.util.List;

/**
 * 加密货币持有Service接口
 * 
 * @author system
 * @date 2024-07-19
 */
public interface CryptoHoldingService {

    /**
     * 通过主键查询加密货币持有
     * 
     * @param id 主键ID
     * @return 加密货币持有信息
     */
    CryptoHoldingVo queryById(Integer id);

    /**
     * 查询加密货币持有列表（分页）
     * 
     * @param cryptoHolding 加密货币持有查询条件
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    List<CryptoHoldingVo> queryPageList(CryptoHoldingBo cryptoHolding, Page pageQuery);

    /**
     * 查询加密货币持有列表（不分页）
     * 
     * @param cryptoHolding 加密货币持有查询条件
     * @return 加密货币持有列表
     */
    List<CryptoHoldingVo> queryList(CryptoHoldingBo cryptoHolding);

    /**
     * 新增加密货币持有
     * 
     * @param cryptoHolding 加密货币持有业务对象
     * @return 新增结果
     */
    boolean insertByBo(CryptoHoldingBo cryptoHolding);

    /**
     * 修改加密货币持有
     * 
     * @param cryptoHolding 加密货币持有业务对象
     * @return 修改结果
     */
    boolean updateByBo(CryptoHoldingBo cryptoHolding);

    /**
     * 批量删除加密货币持有
     * 
     * @param ids 主键集合
     * @return 删除结果
     */
    boolean deleteByIds(Collection<Integer> ids);

    /**
     * 模糊查询加密货币持有
     * 
     * @param keyword 关键字（币种类型等）
     * @return 加密货币持有列表
     */
    List<CryptoHoldingVo> fuzzySearch(String keyword);
}
