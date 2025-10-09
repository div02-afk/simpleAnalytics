# Analytics API - Complete Endpoint Testing Script
# Tests all 10 endpoints with the correct appId

$baseUrl = "http://localhost:8002"
$appId = "99605e23-128b-449b-b02b-f64d1680bf37"
$startDate = "2025-10-01T00:00:00"
$endDate = "2025-10-08T23:59:59"

Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Analytics API Endpoint Tests        ║" -ForegroundColor Cyan
Write-Host "║   AppId: $appId   ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝`n" -ForegroundColor Cyan

$passCount = 0
$failCount = 0

# Test 1: Health Check
Write-Host "[1/10] Health Check" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/health" -Method GET
    Write-Host "  ✅ Status: $($response.status)" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 2: Get Total Event Count
Write-Host "`n[2/10] GET /{appId}/events/count" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/events/count?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Total Events: $($response.data)" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 3: Get Unique User Count
Write-Host "`n[3/10] GET /{appId}/users/count" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/users/count?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Unique Users: $($response.data)" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 4: Get Event Breakdown
Write-Host "`n[4/10] GET /{appId}/events/breakdown" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/events/breakdown?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Event Types: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.eventType): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 5: Get Source Breakdown
Write-Host "`n[5/10] GET /{appId}/sources/breakdown" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/sources/breakdown?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Traffic Sources: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.source): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 6: Get Device Breakdown
Write-Host "`n[6/10] GET /{appId}/context/devices" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/context/devices?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Device Types: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.device_type): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 7: Get Browser Breakdown
Write-Host "`n[7/10] GET /{appId}/context/browsers" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/context/browsers?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Browsers: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.browser): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 8: Get OS Breakdown
Write-Host "`n[8/10] GET /{appId}/context/os" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/context/os?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Operating Systems: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.os): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 9: Get Time Series Data (GET with query params)
Write-Host "`n[9/10] GET /{appId}/timeseries" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/timeseries?granularity=DAY&startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Time Series Data Points: $($response.data.Count)" -ForegroundColor Green
    if ($response.data.Count -gt 0) {
        $response.data | Select-Object -First 3 | ForEach-Object {
            Write-Host "     - $($_.timestamp): $($_.count) events" -ForegroundColor Gray
        }
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Start-Sleep -Milliseconds 300

# Test 10: Get Dashboard Overview
Write-Host "`n[10/10] GET /{appId}/overview" -ForegroundColor Yellow
try {
    $url = "$baseUrl/api/v1/analytics/$appId/overview?startDate=$startDate&endDate=$endDate"
    $response = Invoke-RestMethod -Uri $url -Method GET
    Write-Host "  ✅ Dashboard Overview:" -ForegroundColor Green
    Write-Host "     - Total Events: $($response.data.totalEvents)" -ForegroundColor Gray
    Write-Host "     - Unique Users: $($response.data.uniqueUsers)" -ForegroundColor Gray
    if ($response.data.eventBreakdown) {
        Write-Host "     - Event Types: $($response.data.eventBreakdown.Count)" -ForegroundColor Gray
    }
    if ($response.data.sourceBreakdown) {
        Write-Host "     - Traffic Sources: $($response.data.sourceBreakdown.Count)" -ForegroundColor Gray
    }
    $passCount++
} catch {
    Write-Host "  ❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

# Summary
Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║           Test Summary                 ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host "  ✅ Passed: $passCount" -ForegroundColor Green
Write-Host "  ❌ Failed: $failCount" -ForegroundColor Red
Write-Host "  📊 Total:  10`n" -ForegroundColor White

if ($failCount -eq 0) {
    Write-Host "🎉 All tests passed!" -ForegroundColor Green
} else {
    Write-Host "⚠️  Some tests failed. Check the errors above." -ForegroundColor Yellow
}

Write-Host "`n📚 Swagger UI: http://localhost:8002/swagger-ui.html" -ForegroundColor Cyan
Write-Host "📊 Actuator: http://localhost:8002/actuator/health`n" -ForegroundColor Cyan
