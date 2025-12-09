# 并发测试脚本 - 测试updateHoldings接口的数据一致性

Write-Host "开始测试并发调用updateHoldings接口的数据一致性..."

# 1. 获取当前持仓状态作为基准
Write-Host "\n1. 获取当前持仓状态..."
try {
    $currentHoldings = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/current" -Method GET -UseBasicParsing | ConvertFrom-Json
    Write-Host "当前持仓数据："
    $currentHoldings | Format-Table -AutoSize
} catch {
    Write-Host "获取当前持仓失败: $_" -ForegroundColor Red
    exit 1
} finally {
    # 确保清理任何需要释放的资源
    Write-Host "完成当前持仓状态获取操作"
}

# 2. 准备测试数据 - 为每种币种创建不同的更新请求
Write-Host "\n2. 准备并发测试数据..."

# 创建多个不同的更新请求体
$request1 = @(
    @{
        id = 1
        coinType = "BTC"
        proportion = 45.0
        amount = 1.6
        usdtValue = 65000
        isCurrent = 1
    },
    @{
        id = 2
        coinType = "ETH"
        proportion = 25.0
        amount = 9
        usdtValue = 25000
        isCurrent = 1
    },
    @{
        id = 3
        coinType = "USDT"
        proportion = 30.0
        amount = 30000
        usdtValue = 30000
        isCurrent = 1
    }
)

$request2 = @(
    @{
        id = 1
        coinType = "BTC"
        proportion = 42.0
        amount = 1.55
        usdtValue = 62000
        isCurrent = 1
    },
    @{
        id = 2
        coinType = "ETH"
        proportion = 28.0
        amount = 9.5
        usdtValue = 28000
        isCurrent = 1
    },
    @{
        id = 3
        coinType = "USDT"
        proportion = 30.0
        amount = 30000
        usdtValue = 30000
        isCurrent = 1
    }
)

$request3 = @(
    @{
        id = 1
        coinType = "BTC"
        proportion = 38.0
        amount = 1.4
        usdtValue = 58000
        isCurrent = 1
    },
    @{
        id = 2
        coinType = "ETH"
        proportion = 32.0
        amount = 10.5
        usdtValue = 32000
        isCurrent = 1
    },
    @{
        id = 3
        coinType = "USDT"
        proportion = 30.0
        amount = 30000
        usdtValue = 30000
        isCurrent = 1
    }
)

# 3. 定义并发调用函数
function Invoke-ConcurrentUpdate {
    param(
        [int]$requestId,
        [array]$requestBody
    )
    
    Write-Host "任务 $requestId 开始执行..."
    
    try {
        $jsonBody = $requestBody | ConvertTo-Json -Depth 3
        $response = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/update" -Method POST -ContentType "application/json" -Body $jsonBody -UseBasicParsing
        
        Write-Host "任务 $requestId 完成，状态码: $($response.StatusCode)"
        return @{
            TaskId = $requestId
            Status = "Success"
            StatusCode = $response.StatusCode
            Content = $response.Content
        }
    } catch {
        Write-Host "任务 $requestId 失败: $_" -ForegroundColor Red
        return @{
            TaskId = $requestId
            Status = "Failed"
            ErrorMessage = $_.ToString()
        }
    }
}

# 4. 执行并发请求
Write-Host "\n3. 执行并发请求..."
Write-Host "将发送3个并发请求更新相同的持仓数据"

$jobs = @()

# 创建3个并行任务
$jobs += Start-Job -ScriptBlock { Invoke-ConcurrentUpdate -requestId 1 -requestBody $using:request1 }
$jobs += Start-Job -ScriptBlock { Invoke-ConcurrentUpdate -requestId 2 -requestBody $using:request2 }
$jobs += Start-Job -ScriptBlock { Invoke-ConcurrentUpdate -requestId 3 -requestBody $using:request3 }

# 等待所有任务完成并获取结果
Write-Host "\n等待所有并发任务完成..."
$results = Wait-Job -Job $jobs | Receive-Job

# 显示任务结果摘要
Write-Host "\n4. 并发任务执行结果摘要："
foreach ($result in $results) {
    if ($result.Status -eq "Success") {
        Write-Host "任务 $($result.TaskId): 成功 (状态码: $($result.StatusCode))"
    } else {
        Write-Host "任务 $($result.TaskId): 失败 - $($result.ErrorMessage)" -ForegroundColor Red
    }
}

# 5. 获取最终持仓状态
Write-Host "\n5. 获取最终持仓状态..."
try {
    $finalHoldings = Invoke-WebRequest -Uri "http://localhost:8081/api/holdings/current" -Method GET -UseBasicParsing | ConvertFrom-Json
    Write-Host "最终持仓数据："
    $finalHoldings | Format-Table -AutoSize
} catch {
    Write-Host "获取最终持仓失败: $_" -ForegroundColor Red
}

# 6. 分析结果
Write-Host "\n6. 并发测试分析："

# 检查是否有任务成功
$successCount = ($results | Where-Object { $_.Status -eq "Success" }).Count
Write-Host "成功的请求数: $successCount/3"

# 检查数据一致性
Write-Host "\n数据一致性检查："
if ($finalHoldings -and $finalHoldings.Count -gt 0) {
    # 计算各币种总和
    $totalProportion = 0
    $totalUsdtValue = 0
    
    foreach ($holding in $finalHoldings) {
        $totalProportion += $holding.proportion
        $totalUsdtValue += $holding.usdtValue
    }
    
    Write-Host "各币种比例总和: $totalProportion%"
    Write-Host "总价值: $totalUsdtValue USDT"
    
    # 检查比例总和是否接近100
    if ([math]::Abs($totalProportion - 100) -lt 1) {
        Write-Host "✅ 比例总和正常（接近100%）"
    } else {
        Write-Host "❌ 比例总和异常（与100%相差较大）" -ForegroundColor Red
    }
    
    # 显示最终结果与各个请求的匹配情况
    Write-Host "\n最终结果与请求数据匹配分析："
    
    # 检查是否匹配请求1
    $matchRequest1 = $true
    foreach ($holding in $finalHoldings) {
        $request1Holding = $request1 | Where-Object { $_.coinType -eq $holding.coinType }
        if ($request1Holding -and ($request1Holding.proportion -ne $holding.proportion -or $request1Holding.amount -ne $holding.amount)) {
            $matchRequest1 = $false
            break
        }
    }
    
    # 检查是否匹配请求2
    $matchRequest2 = $true
    foreach ($holding in $finalHoldings) {
        $request2Holding = $request2 | Where-Object { $_.coinType -eq $holding.coinType }
        if ($request2Holding -and ($request2Holding.proportion -ne $holding.proportion -or $request2Holding.amount -ne $holding.amount)) {
            $matchRequest2 = $false
            break
        }
    }
    
    # 检查是否匹配请求3
    $matchRequest3 = $true
    foreach ($holding in $finalHoldings) {
        $request3Holding = $request3 | Where-Object { $_.coinType -eq $holding.coinType }
        if ($request3Holding -and ($request3Holding.proportion -ne $holding.proportion -or $request3Holding.amount -ne $holding.amount)) {
            $matchRequest3 = $false
            break
        }
    }
    
    # 输出匹配结果
    if ($matchRequest1) { Write-Host "✅ 最终结果匹配请求1" }
    if ($matchRequest2) { Write-Host "✅ 最终结果匹配请求2" }
    if ($matchRequest3) { Write-Host "✅ 最终结果匹配请求3" }
    
    if (-not ($matchRequest1 -or $matchRequest2 -or $matchRequest3)) {
        Write-Host "❌ 最终结果不匹配任何请求数据，可能存在数据不一致" -ForegroundColor Red
    }
} catch {
    Write-Host "分析最终持仓数据时出错: $_" -ForegroundColor Red
} else {
    Write-Host "❌ 无法获取有效的最终持仓数据进行分析" -ForegroundColor Red
}

Write-Host "\n并发测试完成！"
Write-Host "\n结论："
if ($successCount -eq 3) {
    Write-Host "多个请求同时成功，但需要检查最终数据是否与预期一致。在没有适当并发控制的情况下，最后一个写入的数据通常会覆盖之前的数据。"
} elseif ($successCount -eq 0) {
    Write-Host "所有请求都失败了，可能是因为并发冲突或其他错误。"
} else {
    Write-Host "部分请求成功，部分失败，这可能表明系统对并发请求有一定的处理机制，但可能不够完善。"
}

Write-Host "\n建议："
Write-Host "1. 考虑在实体类中添加@Version字段实现乐观锁"
Write-Host "2. 或在service层使用悲观锁（如@Lock注解）保护关键数据"
Write-Host "3. 确保事务隔离级别适当配置"
Write-Host "4. 添加更详细的日志记录以便追踪并发问题"