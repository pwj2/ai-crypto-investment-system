package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.wms.domain.bo.CryptoHoldingBo;
import com.ruoyi.wms.domain.vo.CryptoHoldingVo;
import com.ruoyi.wms.service.CryptoHoldingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * 加密货币持有Controller
 * 
 * @author system
 * @date 2024-07-19
 */
@RestController
@RequestMapping("/wms/crypto")
@RequiredArgsConstructor
public class CryptoHoldingController {

    private final CryptoHoldingService cryptoHoldingService;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询加密货币持有列表（分页）
     */
    @GetMapping("/list")
    public R<List<CryptoHoldingVo>> list(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        CryptoHoldingBo cryptoHolding) {
        Page<CryptoHoldingVo> page = new Page<>(pageNum, pageSize);
        List<CryptoHoldingVo> list = cryptoHoldingService.queryPageList(cryptoHolding, page);
        return R.ok(list);
    }

    /**
     * 根据ID查询加密货币持有详情
     */
    @GetMapping("/{id}")
    public R<CryptoHoldingVo> getInfo(@PathVariable("id") Integer id) {
        return R.ok(cryptoHoldingService.queryById(id));
    }

    /**
     * 模糊查询加密货币持有
     */
    @GetMapping("/search")
    public R<List<CryptoHoldingVo>> search(@RequestParam("keyword") String keyword) {
        return R.ok(cryptoHoldingService.fuzzySearch(keyword));
    }

    /**
     * 新增加密货币持有
     */
    @PostMapping
    public R<Void> add(@RequestBody CryptoHoldingBo cryptoHolding) {
        return toAjax(cryptoHoldingService.insertByBo(cryptoHolding));
    }

    /**
     * 根据ID修改加密货币持有
     */
    @PutMapping
    public R<Void> edit(@RequestBody CryptoHoldingBo cryptoHolding) {
        return toAjax(cryptoHoldingService.updateByBo(cryptoHolding));
    }

    /**
     * 根据ID删除加密货币持有
     */
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        return toAjax(cryptoHoldingService.deleteByIds(List.of(id)));
    }

    /**
     * 批量删除加密货币持有
     */
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestBody List<Integer> ids) {
        return toAjax(cryptoHoldingService.deleteByIds(ids));
    }

    /**
     * 结果转换
     */
    private R<Void> toAjax(boolean result) {
        return result ? R.ok() : R.fail();
    }
    
    /**
     * 获取加密货币市场分析信息
     * 使用Dify AI API分析加密货币市场数据
     */
    @GetMapping("/market-analysis")
    public R<Object> getMarketAnalysis(@RequestParam(required = false) String query, 
                                     @RequestParam(required = false, defaultValue = "123") String number) {
        try {
            // Dify API配置
            String apiUrl = "https://api.dify.ai/v1/chat-messages";
            String apiKey = "app-91RFuNNYkIInHXcP0jN4YopB"; // 从用户提供的Authorization Bearer中提取
            
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            // 创建请求体
            Map<String, Object> requestBody = new HashMap<>();
            
            // 设置inputs参数
            Map<String, String> inputs = new HashMap<>();
            inputs.put("qkl", "https://www.tradingkey.com/zh-hans/markets/cryptocurrencies");
            inputs.put("number", number);
            requestBody.put("inputs", inputs);
            
            // 设置其他参数
            requestBody.put("query", query != null ? query : "请分析市场中的加密货币信息");
            requestBody.put("response_mode", "streaming");
            requestBody.put("conversation_id", "");
            requestBody.put("user", "postman_test_qkl");
            
            // 创建HTTP请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求并获取响应
            ResponseEntity<Object> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );
            
            return R.ok(response.getBody());
        } catch (Exception e) {
            return R.fail("获取加密货币市场分析失败: " + e.getMessage());
        }
    }
}
