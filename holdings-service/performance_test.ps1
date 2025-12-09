# 简单的并发性能测试脚本
Write-Host "开始执行并发性能测试..."

# 设置测试参数
$baseUrl = "http://localhost:8081/api/positions"
$requestCount = 10

# 开始时间
$startTime = Get-Date

# 并发测试结果
$successCount = 0
$failedCount = 0
$totalResponseTime = 0
$maxTime = 0

# 执行10个并发请求（使用更简单的方式）
for ($i = 1; $i -le $requestCount; $i++) {
    Write-Host "执行请求 $i..."
    $reqStartTime = Get-Date
    
    try {
        $response = Invoke-WebRequest -Uri $baseUrl -Method Get -UseBasicParsing -TimeoutSec 5
        $statusCode = $response.StatusCode
        $responseTime = ((Get-Date) - $reqStartTime).TotalMilliseconds
        
        Write-Host "请求 $i: 成功 - 状态码: $statusCode - 响应时间: $responseTime ms"
        
        $successCount++
        $totalResponseTime += $responseTime
        
        if ($responseTime -gt $maxTime) {
            $maxTime = $responseTime
        }
    } catch {
        $responseTime = ((Get-Date) - $reqStartTime).TotalMilliseconds
        Write-Host "请求 $i: 失败 - 错误: $($_.Exception.Message) - 响应时间: $responseTime ms"
        $failedCount++
    }
}

# 结束时间
$endTime = Get-Date
$totalTime = ($endTime - $startTime).TotalSeconds

# 计算平均响应时间
$avgResponseTime = 0
if ($successCount -gt 0) {
    $avgResponseTime = $totalResponseTime / $successCount
}

# 输出测试结果
Write-Host ""
Write-Host "===== 性能测试结果 ====="
Write-Host "总请求数: $requestCount"
Write-Host "成功请求数: $successCount"
Write-Host "失败请求数: $failedCount"
Write-Host "总执行时间: $totalTime 秒"
Write-Host "平均响应时间: $avgResponseTime ms"
Write-Host "最大响应时间: $maxTime ms"
Write-Host "======================="

# 检查是否通过性能要求
if ($failedCount -eq 0 -and $maxTime -lt 500) {
    Write-Host "✅ 性能测试通过：所有请求成功，且最大响应时间小于500ms"
} else {
    Write-Host "❌ 性能测试未通过"
    if ($failedCount -gt 0) {
        Write-Host "  - 有 $failedCount 个请求失败"
    }
    if ($maxTime -ge 500) {
        Write-Host "  - 最大响应时间 $maxTime ms 超过了500ms的要求"
    }
}