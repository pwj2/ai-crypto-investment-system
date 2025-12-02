package com.ruoyi.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.wms.domain.bo.CryptoHoldingBo;
import com.ruoyi.wms.domain.entity.CryptoHolding;
import com.ruoyi.wms.domain.vo.CryptoHoldingVo;
import com.ruoyi.wms.mapper.CryptoHoldingMapper;
import com.ruoyi.wms.service.CryptoHoldingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/**
 * 加密货币持有Service实现
 * 
 * @author system
 * @date 2024-07-19
 */
@Service
@RequiredArgsConstructor
public class CryptoHoldingServiceImpl implements CryptoHoldingService {

    private final CryptoHoldingMapper cryptoHoldingMapper;

    /**
     * 通过主键查询加密货币持有
     */
    @Override
    public CryptoHoldingVo queryById(Integer id) {
        return cryptoHoldingMapper.selectVoById(id);
    }

    /**
     * 查询加密货币持有列表（分页）
     */
    @Override
    public List<CryptoHoldingVo> queryPageList(CryptoHoldingBo cryptoHolding, Page pageQuery) {
        LambdaQueryWrapper<CryptoHolding> lqw = buildQueryWrapper(cryptoHolding);
        return cryptoHoldingMapper.selectVoPage(pageQuery, lqw).getRecords();
    }

    /**
     * 查询加密货币持有列表（不分页）
     */
    @Override
    public List<CryptoHoldingVo> queryList(CryptoHoldingBo cryptoHolding) {
        LambdaQueryWrapper<CryptoHolding> lqw = buildQueryWrapper(cryptoHolding);
        return cryptoHoldingMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CryptoHolding> buildQueryWrapper(CryptoHoldingBo cryptoHolding) {
        LambdaQueryWrapper<CryptoHolding> lqw = Wrappers.lambdaQuery();
        lqw.eq(Objects.nonNull(cryptoHolding.getId()), CryptoHolding::getId, cryptoHolding.getId());
        lqw.eq(Objects.nonNull(cryptoHolding.getCoinType()), CryptoHolding::getCoinType, cryptoHolding.getCoinType());
        lqw.eq(Objects.nonNull(cryptoHolding.getAmount()), CryptoHolding::getAmount, cryptoHolding.getAmount());
        lqw.eq(Objects.nonNull(cryptoHolding.getPercentage()), CryptoHolding::getPercentage, cryptoHolding.getPercentage());
        lqw.eq(Objects.nonNull(cryptoHolding.getCurrentPrice()), CryptoHolding::getCurrentPrice, cryptoHolding.getCurrentPrice());
        lqw.eq(Objects.nonNull(cryptoHolding.getTotalValue()), CryptoHolding::getTotalValue, cryptoHolding.getTotalValue());
        lqw.like(Objects.nonNull(cryptoHolding.getRemark()), CryptoHolding::getRemark, cryptoHolding.getRemark());
        // 使用关键字进行模糊查询
        if (Objects.nonNull(cryptoHolding.getKeyword())) {
            lqw.and(wrapper -> wrapper
                .like(CryptoHolding::getCoinType, cryptoHolding.getKeyword())
                .or().like(CryptoHolding::getRemark, cryptoHolding.getKeyword())
                .or().like(CryptoHolding::getCreateBy, cryptoHolding.getKeyword())
            );
        }
        return lqw;
    }

    /**
     * 新增加密货币持有
     */
    @Override
    public boolean insertByBo(CryptoHoldingBo cryptoHolding) {
        // 手动转换Bo到实体类
        CryptoHolding entity = new CryptoHolding();
        if (cryptoHolding.getId() != null) {
            entity.setId(cryptoHolding.getId().longValue());
        }
        entity.setCoinType(cryptoHolding.getCoinType());
        entity.setAmount(cryptoHolding.getAmount());
        entity.setPercentage(cryptoHolding.getPercentage());
        entity.setCurrentPrice(cryptoHolding.getCurrentPrice());
        entity.setTotalValue(cryptoHolding.getTotalValue());
        entity.setRemark(cryptoHolding.getRemark());
        entity.setCreateBy(cryptoHolding.getCreateBy());
        return cryptoHoldingMapper.insert(entity) > 0;
    }

    /**
     * 修改加密货币持有
     */
    @Override
    public boolean updateByBo(CryptoHoldingBo cryptoHolding) {
        // 手动转换Bo到实体类
        CryptoHolding entity = new CryptoHolding();
        if (cryptoHolding.getId() != null) {
            entity.setId(cryptoHolding.getId().longValue());
        }
        entity.setCoinType(cryptoHolding.getCoinType());
        entity.setAmount(cryptoHolding.getAmount());
        entity.setPercentage(cryptoHolding.getPercentage());
        entity.setCurrentPrice(cryptoHolding.getCurrentPrice());
        entity.setTotalValue(cryptoHolding.getTotalValue());
        entity.setRemark(cryptoHolding.getRemark());
        entity.setUpdateBy(cryptoHolding.getUpdateBy());
        return cryptoHoldingMapper.updateById(entity) > 0;
    }

    /**
     * 批量删除加密货币持有
     */
    @Override
    public boolean deleteByIds(Collection<Integer> ids) {
        return cryptoHoldingMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 模糊查询加密货币持有
     */
    @Override
    public List<CryptoHoldingVo> fuzzySearch(String keyword) {
        LambdaQueryWrapper<CryptoHolding> lqw = Wrappers.lambdaQuery();
        if (keyword != null && !keyword.isEmpty()) {
            lqw.like(CryptoHolding::getCoinType, keyword)
               .or().like(CryptoHolding::getRemark, keyword)
               .or().like(CryptoHolding::getCreateBy, keyword);
        }
        return cryptoHoldingMapper.selectVoList(lqw);
    }
}
