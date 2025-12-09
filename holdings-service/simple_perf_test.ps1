# Simple performance test script
Write-Host "Starting performance test..."

# 使用正确的接口路径 - 持仓查询接口
$baseUrl = "http://localhost:8081/api/holdings"
$count = 10
$success = 0
$totalTime = 0
$maxTime = 0

for ($i=1; $i -le $count; $i++) {
    $start = Get-Date
    try {
        $response = Invoke-WebRequest -Uri $baseUrl -Method Get -UseBasicParsing -TimeoutSec 5
        $time = ((Get-Date) - $start).TotalMilliseconds
        $success++
        $totalTime += $time
        if ($time -gt $maxTime) { $maxTime = $time }
        Write-Host "Request $i Success - $time ms"
    } catch {
        $time = ((Get-Date) - $start).TotalMilliseconds
        Write-Host "Request $i Failed - $time ms"
        Write-Host "Error: $_"
    }
}

Write-Host ""
Write-Host "Test Results:"
Write-Host "Success: $success / $count"

if ($success -gt 0) {
    $avgTime = $totalTime / $success
    Write-Host "Avg Time: $avgTime ms"
} else {
    Write-Host "Avg Time: N/A"
}

Write-Host "Max Time: $maxTime ms"

if ($success -eq $count -and $maxTime -lt 500) {
    Write-Host "PASS: All requests succeeded with max time < 500ms"
} else {
    Write-Host "FAIL: Performance requirements not met"
}