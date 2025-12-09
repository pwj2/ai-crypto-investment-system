@echo off

setlocal enabledelayedexpansion

echo 开始测试并发调用updateHoldings接口...
echo.

REM 1. 准备测试数据文件
echo 创建测试数据文件...

REM 创建请求数据文件1
> request1.json echo [
>> request1.json echo   {"id":1,"coinType":"BTC","proportion":45.0,"amount":1.6,"usdtValue":65000,"isCurrent":1},
>> request1.json echo   {"id":2,"coinType":"ETH","proportion":25.0,"amount":9,"usdtValue":25000,"isCurrent":1},
>> request1.json echo   {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
>> request1.json echo ]

REM 创建请求数据文件2
> request2.json echo [
>> request2.json echo   {"id":1,"coinType":"BTC","proportion":42.0,"amount":1.55,"usdtValue":62000,"isCurrent":1},
>> request2.json echo   {"id":2,"coinType":"ETH","proportion":28.0,"amount":9.5,"usdtValue":28000,"isCurrent":1},
>> request2.json echo   {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
>> request2.json echo ]

REM 创建请求数据文件3
> request3.json echo [
>> request3.json echo   {"id":1,"coinType":"BTC","proportion":38.0,"amount":1.4,"usdtValue":58000,"isCurrent":1},
>> request3.json echo   {"id":2,"coinType":"ETH","proportion":32.0,"amount":10.5,"usdtValue":32000,"isCurrent":1},
>> request3.json echo   {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
>> request3.json echo ]

echo 数据文件创建完成。
echo.

REM 2. 获取初始状态
echo 获取初始持仓状态...
curl -s http://localhost:8081/api/holdings/current > initial_holdings.json
echo 初始持仓状态已保存到 initial_holdings.json
echo.

REM 3. 执行并发请求
echo 执行3个并发请求...
echo 启动请求1...
start /min cmd /c "curl -s -X POST -H "Content-Type: application/json" -d @request1.json http://localhost:8081/api/holdings/update > response1.txt 2>&1"
echo 启动请求2...
start /min cmd /c "curl -s -X POST -H "Content-Type: application/json" -d @request2.json http://localhost:8081/api/holdings/update > response2.txt 2>&1"
echo 启动请求3...
start /min cmd /c "curl -s -X POST -H "Content-Type: application/json" -d @request3.json http://localhost:8081/api/holdings/update > response3.txt 2>&1"

echo 等待所有请求完成...
ping 127.0.0.1 -n 5 > nul

echo.
echo 并发请求已发送，检查响应结果...
set success_count=0

if exist response1.txt (
    set /p resp1=<response1.txt
    if not "!resp1!"=="" (
        echo 请求1响应: !resp1!
        set /a success_count+=1
    ) else (
        echo 请求1无响应或失败
    )
)

if exist response2.txt (
    set /p resp2=<response2.txt
    if not "!resp2!"=="" (
        echo 请求2响应: !resp2!
        set /a success_count+=1
    ) else (
        echo 请求2无响应或失败
    )
)

if exist response3.txt (
    set /p resp3=<response3.txt
    if not "!resp3!"=="" (
        echo 请求3响应: !resp3!
        set /a success_count+=1
    ) else (
        echo 请求3无响应或失败
    )
)

echo.
echo 成功的请求数: %success_count%/3

echo.
echo 获取最终持仓状态...
curl -s http://localhost:8081/api/holdings/current > final_holdings.json
echo 最终持仓状态已保存到 final_holdings.json
echo.
echo 查看最终持仓数据:
type final_holdings.json
echo.
echo 并发测试完成！
echo.
echo 结论:
if %success_count% equ 3 (
    echo 多个请求同时成功，但在没有适当并发控制的情况下，最后一个写入的数据通常会覆盖之前的数据。
    echo 这可能导致数据不一致问题。
) else if %success_count% equ 0 (
    echo 所有请求都失败了，可能是因为并发冲突或其他错误。
) else (
    echo 部分请求成功，部分失败。
)

echo.
echo 建议的并发控制改进:
echo 1. 在Holdings实体类中添加@Version字段实现乐观锁
echo 2. 使用@Transactional(isolation = Isolation.SERIALIZABLE)提高事务隔离级别
echo 3. 在updateHoldings方法中实现基于版本的检查逻辑

REM 清理临时文件
del /q request1.json request2.json request3.json response1.txt response2.txt response3.txt

pause