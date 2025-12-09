# 极简版并发测试脚本

Write-Host "开始测试并发调用updateHoldings接口..."

# 1. 准备测试数据
$request1 = @"
[
  {"id":1,"coinType":"BTC","proportion":45.0,"amount":1.6,"usdtValue":65000,"isCurrent":1},
  {"id":2,"coinType":"ETH","proportion":25.0,"amount":9,"usdtValue":25000,"isCurrent":1},
  {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
]
"@

$request2 = @"
[
  {"id":1,"coinType":"BTC","proportion":42.0,"amount":1.55,"usdtValue":62000,"isCurrent":1},
  {"id":2,"coinType":"ETH","proportion":28.0,"amount":9.5,"usdtValue":28000,"isCurrent":1},
  {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
]
"@

$request3 = @"
[
  {"id":1,"coinType":"BTC","proportion":38.0,"amount":1.4,"usdtValue":58000,"isCurrent":1},
  {"id":2,"coinType":"ETH","proportion":32.0,"amount":10.5,"usdtValue":32000,"isCurrent":1},
  {"id":3,"coinType":"USDT","proportion":30.0,"amount":30000,"usdtValue":30000,"isCurrent":1}
]
"@

# 2. 定义一个简单的HTTP请求函数
function Send-UpdateRequest {
    param(
        [string]$requestBody,
        [int]$requestId
    )
    
    Write-Host "发送请求 $requestId..."
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/update" -Method POST -ContentType "application/json" -Body $requestBody -UseBasicParsing
        Write-Host "请求 $requestId 完成，状态码: $($response.StatusCode)"
        return $true
    } catch {
        Write-Host "请求 $requestId 失败: $_" -ForegroundColor Red
        return $false
    }
}

# 3. 获取初始状态
Write-Host "\n获取初始持仓状态..."
try {
    $initialResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/current" -Method GET -UseBasicParsing
    Write-Host "初始持仓状态获取成功"
} catch {
    Write-Host "获取初始持仓失败: $_" -ForegroundColor Red
}

# 4. 执行并发请求
Write-Host "\n执行3个并发请求..."

# 创建3个作业
$job1 = Start-Job -ScriptBlock { param($reqBody) Send-UpdateRequest -requestBody $reqBody -requestId 1 } -ArgumentList $request1
$job2 = Start-Job -ScriptBlock { param($reqBody) Send-UpdateRequest -requestBody $reqBody -requestId 2 } -ArgumentList $request2
$job3 = Start-Job -ScriptBlock { param($reqBody) Send-UpdateRequest -requestBody $reqBody -requestId 3 } -ArgumentList $request3

# 等待作业完成
Write-Host "等待所有请求完成..."
Wait-Job -Job $job1, $job2, $job3

# 获取结果
$results = @()
$results += Receive-Job -Job $job1
$results += Receive-Job -Job $job2
$results += Receive-Job -Job $job3

# 清理作业
Remove-Job -Job $job1, $job2, $job3

# 5. 统计成功请求数
$successCount = ($results | Where-Object { $_ -eq $true }).Count
Write-Host "\n成功的请求数: $successCount/3"

# 6. 获取最终状态
Write-Host "\n获取最终持仓状态..."
try {
    $finalResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/current" -Method GET -UseBasicParsing
    $finalHoldings = $finalResponse.Content | ConvertFrom-Json
    Write-Host "最终持仓数据："
    $finalHoldings | Format-Table -AutoSize
    
    # 计算各币种比例总和
    $totalProportion = 0
    foreach ($holding in $finalHoldings) {
        $totalProportion += $holding.proportion
    }
    Write-Host "\n各币种比例总和: $totalProportion%"
    
    # 检查比例总和是否正常
    if ([math]::Abs($totalProportion - 100) -lt 1) {
        Write-Host "✅ 比例总和正常（接近100%）"
    } else {
        Write-Host "❌ 比例总和异常（与100%相差较大）" -ForegroundColor Red
    }
} catch {
    Write-Host "获取最终持仓失败: $_" -ForegroundColor Red
}

# 7. 结论
Write-Host "\n并发测试完成！"
Write-Host "\n结论："
if ($successCount -eq 3) {
    Write-Host "多个请求同时成功，但在没有适当并发控制的情况下，最后一个写入的数据通常会覆盖之前的数据。"
    Write-Host "这可能导致数据不一致问题。"
} elseif ($successCount -eq 0) {
    Write-Host "所有请求都失败了，可能是因为并发冲突或其他错误。"
} else {
    Write-Host "部分请求成功，部分失败。"
}

Write-Host "\n建议的并发控制改进："
Write-Host "1. 在Holdings实体类中添加@Version字段实现乐观锁"
Write-Host "2. 使用@Transactional(isolation = Isolation.SERIALIZABLE)提高事务隔离级别"
Write-Host "3. 在updateHoldings方法中实现基于版本的检查逻辑"