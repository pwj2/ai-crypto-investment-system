package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.service.HoldingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 加密货币持有信息Controller
 */
@RestController
@RequestMapping("/api/holdings")
public class HoldingsController {

    @Autowired
    private HoldingsService holdingsService;

    /**
     * 查询所有持仓（GET请求）
     */
    @GetMapping
    public List<Holdings> getAllHoldings() {
        return holdingsService.getAllHoldings();
    }
    
    /**
     * 查询当前持仓（GET请求）
     */
    @GetMapping("/current")
    public List<Holdings> getCurrentHoldings() {
        return holdingsService.getCurrentHoldings();
    }

    // 1. 更新持仓（需审核通过后调用）
    @PostMapping("/update")
    public ResponseEntity<String> updateHoldings(@RequestBody List<Holdings> newHoldings) {
        boolean success = holdingsService.updateHoldings(newHoldings);
        return success ? ResponseEntity.ok("持仓更新成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("持仓更新失败");
    }
    
    /**
     * 根据币种类型查询资产信息
     */
    @GetMapping("/coin/{coinType}")
    public ResponseEntity<Holdings> getHoldingsByCoinType(@PathVariable String coinType) {
        return holdingsService.findByCoinType(coinType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 分页查询资产信息
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Holdings>> getHoldingsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Holdings> holdingsPage = holdingsService.findAll(pageable);
        return ResponseEntity.ok(holdingsPage);
    }

    /**
     * 更新持仓（PUT请求，保持兼容性）
     */
    @PutMapping
    public String updateHoldingsLegacy(@RequestBody List<Holdings> holdingsList) {
        boolean success = holdingsService.updateHoldings(holdingsList);
        return success ? "持仓更新成功" : "持仓更新失败";
    }

    /**
     * 根据ID查询资产信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Holdings> getHoldingsById(@PathVariable Long id) {
        return holdingsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新增资产信息
     */
    @PostMapping
    public ResponseEntity<Holdings> createHoldings(@RequestBody Holdings holdings) {
        Holdings saved = holdingsService.save(holdings);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 更新资产信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Holdings> updateHoldingsById(@PathVariable Long id, @RequestBody Holdings holdings) {
        // 验证请求体
        if (holdings == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // 检查资产是否存在
        Optional<Holdings> existingHoldings = holdingsService.findById(id);
        if (!existingHoldings.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // 更新资产信息
        Holdings updatedHoldings = holdingsService.update(holdings);
        return ResponseEntity.ok(updatedHoldings);
    }

    /**
     * 删除资产信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoldings(@PathVariable Long id) {
        if (!holdingsService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        holdingsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除资产信息
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteHoldingsBatch(@RequestBody List<Long> ids) {
        holdingsService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 调整持仓（根据AI建议）
     */
    @PostMapping("/adjust")
    public ResponseEntity<String> adjustHoldings(@RequestBody String adjustSuggest) {
        try {
            holdingsService.adjustHoldings(adjustSuggest);
            return ResponseEntity.ok("持仓调整成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("持仓调整失败: " + e.getMessage());
        }
    }
}