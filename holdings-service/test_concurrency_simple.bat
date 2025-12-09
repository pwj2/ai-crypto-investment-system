@echo off
setlocal enabledelayedexpansion

:: 设置基础URL
set BASE_URL=http://localhost:8080/api/holdings

:: 1. 获取初始持仓状态
echo 获取初始持仓状态...
curl -s "%BASE_URL%/all" > initial_holdings.json
echo 初始持仓状态已保存到 initial_holdings.json

:: 2. 准备测试数据
set "DATA1=[{\"coinType\":\"BTC\",\"amount\":100,\"proportion\":0.5},{\"coinType\":\"ETH\",\"amount\":200,\"proportion\":0.5}]"
set "DATA2=[{\"coinType\":\"BTC\",\"amount\":150,\"proportion\":0.6},{\"coinType\":\"ETH\",\"amount\":100,\"proportion\":0.4}]"
set "DATA3=[{\"coinType\":\"BTC\",\"amount\":200,\"proportion\":0.7},{\"coinType\":\"ETH\",\"amount\":85,\"proportion\":0.3}]"

:: 3. 创建三个并发请求
echo 开始并发调用更新持仓接口...
start cmd /c "curl -s -X POST -H "Content-Type: application/json" -d %DATA1% "%BASE_URL%/update" > response1.json && echo 请求1完成"
start cmd /c "curl -s -X POST -H "Content-Type: application/json" -d %DATA2% "%BASE_URL%/update" > response2.json && echo 请求2完成"
start cmd /c "curl -s -X POST -H "Content-Type: application/json" -d %DATA3% "%BASE_URL%/update" > response3.json && echo 请求3完成"

:: 4. 等待所有请求完成
echo 等待所有并发请求完成...
timeout /t 5 > nul

:: 5. 获取最终持仓状态
echo 获取最终持仓状态...
curl -s "%BASE_URL%/all" > final_holdings.json
echo 最终持仓状态已保存到 final_holdings.json

:: 6. 输出测试结果
echo 并发测试完成！
echo 请查看以下文件分析结果：
echo - initial_holdings.json (初始状态)
echo - final_holdings.json (最终状态)
echo - response1.json, response2.json, response3.json (各请求响应)

echo 测试结论：由于代码中updateHoldings方法没有版本字段或显式的并发控制机制，可能存在数据覆盖问题。