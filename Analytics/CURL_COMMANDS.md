# cURL Commands for Postman Testing

**Base URL:** `http://localhost:8002`  
**AppId:** `99605e23-128b-449b-b02b-f64d1680bf37`  
**Date Range:** Oct 1-8, 2025

---

## ✅ Working Endpoints

### 1. Health Check
```bash
curl http://localhost:8002/api/v1/health
```

### 2. Total Event Count
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/events/count?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 3. Unique User Count
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/users/count?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 4. Event Type Breakdown
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/events/breakdown?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 5. Traffic Source Breakdown
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/sources/breakdown?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

---

## ⚠️ Endpoints with Timeouts (Need Optimization)

### 6. Device Breakdown (408 Timeout)
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/context/devices?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 7. Browser Breakdown (408 Timeout)
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/context/browsers?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 8. OS Breakdown (408 Timeout)
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/context/os?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

### 10. Dashboard Overview (408 Timeout)
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/overview?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

---

## ✅ Time Series Endpoint (Now Working!)

### 9. Time Series Data
```bash
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/timeseries?granularity=DAY&startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

**Available Granularities:**
- `MINUTE` - Group by minute
- `HOUR` - Group by hour  
- `DAY` - Group by day
- `WEEK` - Group by week
- `MONTH` - Group by month

**Examples:**
```bash
# Hourly data for last 2 days
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/timeseries?granularity=HOUR&startDate=2025-10-07T00:00:00&endDate=2025-10-08T23:59:59"

# Weekly data for last month
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/timeseries?granularity=WEEK&startDate=2025-09-01T00:00:00&endDate=2025-10-08T23:59:59"

# Monthly data for last year
curl "http://localhost:8002/api/v1/analytics/99605e23-128b-449b-b02b-f64d1680bf37/timeseries?granularity=MONTH&startDate=2024-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

---

## 📝 Notes

- All GET endpoints use query parameters: `?startDate=...&endDate=...`
- Only `timeseries` endpoint uses POST with JSON body
- Date format: ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)
- All responses wrapped in `AnalyticsResponse<T>` with `data` field

---

## 🔧 For PowerShell

Use `Invoke-RestMethod` instead:

```powershell
$appId = "99605e23-128b-449b-b02b-f64d1680bf37"
$start = "2025-10-01T00:00:00"
$end = "2025-10-08T23:59:59"

# Example: Event Count
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/count?startDate=$start&endDate=$end"
```

---

**Run all tests:** `.\test-all-endpoints.ps1`
