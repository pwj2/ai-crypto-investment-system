package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.HoldingsHistory;
import com.digitalcoin.holdings_service.service.HoldingsHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产变动历史Controller
 */
@RestController
@RequestMapping("/api/holdings-history")
public class HoldingsHistoryController {

    @Autowired
    private HoldingsHistoryService holdingsHistoryService;

    /**
     * 根据ID查询资产变动历史
     */
    @GetMapping("/{id}")
    public ResponseEntity<HoldingsHistory> getHistoryById(@PathVariable Long id) {
        return holdingsHistoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据用户ID分页查询资产变动历史
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<HoldingsHistory>> getHistoryByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoldingsHistory> historyPage = holdingsHistoryService.findByUserId(userId, pageable);
        return ResponseEntity.ok(historyPage);
    }

    /**
     * 根据用户ID和资产代码查询历史记录
     */
    @GetMapping("/user/{userId}/coin/{coinType}")
    public ResponseEntity<List<HoldingsHistory>> getHistoryByUserIdAndCoinType(
            @PathVariable Long userId,
            @PathVariable String coinType) {
        List<HoldingsHistory> histories = holdingsHistoryService.findByUserIdAndCoinType(userId, coinType);
        return ResponseEntity.ok(histories);
    }

    /**
     * 根据用户ID和交易类型查询历史记录
     */
    @GetMapping("/user/{userId}/type/{transactionType}")
    public ResponseEntity<List<HoldingsHistory>> getHistoryByUserIdAndTransactionType(
            @PathVariable Long userId,
            @PathVariable String transactionType) {
        List<HoldingsHistory> histories = holdingsHistoryService.findByUserIdAndTransactionType(userId, transactionType);
        return ResponseEntity.ok(histories);
    }

    /**
     * 根据时间范围查询历史记录
     */
    @GetMapping("/user/{userId}/time-range")
    public ResponseEntity<List<HoldingsHistory>> getHistoryByTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<HoldingsHistory> histories = holdingsHistoryService.findByUserIdAndTransactionTimeBetween(userId, startTime, endTime);
        return ResponseEntity.ok(histories);
    }

    /**
     * 保存资产变动历史记录
     */
    @PostMapping
    public ResponseEntity<HoldingsHistory> createHistory(@RequestBody HoldingsHistory history) {
        HoldingsHistory saved = holdingsHistoryService.save(history);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 批量保存资产变动历史记录
     */
    @PostMapping("/batch")
    public ResponseEntity<List<HoldingsHistory>> createHistoriesBatch(@RequestBody List<HoldingsHistory> histories) {
        List<HoldingsHistory> saved = holdingsHistoryService.saveAll(histories);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 删除资产变动历史记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        holdingsHistoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除资产变动历史记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteHistoriesBatch(@RequestBody List<Long> ids) {
        holdingsHistoryService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取指定交易类型的总金额
     */
    @GetMapping("/user/{userId}/total-amount/{transactionType}")
    public ResponseEntity<BigDecimal> getTotalAmountByTransactionType(
            @PathVariable Long userId,
            @PathVariable String transactionType) {
        BigDecimal totalAmount = holdingsHistoryService.getTotalAmountByUserIdAndTransactionType(userId, transactionType);
        return ResponseEntity.ok(totalAmount);
    }

    /**
     * 创建买入记录
     */
    @PostMapping("/buy")
    public ResponseEntity<HoldingsHistory> createBuyRecord(
            @RequestParam Long userId,
            @RequestParam String coinType,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description) {
        HoldingsHistory history = holdingsHistoryService.createBuyRecord(userId, coinType, quantity, price, description);
        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }

    /**
     * 创建卖出记录
     */
    @PostMapping("/sell")
    public ResponseEntity<HoldingsHistory> createSellRecord(
            @RequestParam Long userId,
            @RequestParam String coinType,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description) {
        HoldingsHistory history = holdingsHistoryService.createSellRecord(userId, coinType, quantity, price, description);
        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }

    /**
     * 创建转账记录
     */
    @PostMapping("/transfer")
    public ResponseEntity<HoldingsHistory> createTransferRecord(
            @RequestParam Long userId,
            @RequestParam String coinType,
            @RequestParam BigDecimal quantity,
            @RequestParam String direction,
            @RequestParam(required = false) String description) {
        HoldingsHistory history = holdingsHistoryService.createTransferRecord(userId, coinType, quantity, direction, description);
        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }

    /**
     * 清理旧历史记录
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        holdingsHistoryService.cleanupHistoryBefore(beforeTime);
        return ResponseEntity.noContent().build();
    }
}