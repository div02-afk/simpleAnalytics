# Analytics Service - Quick Start Guide

## 🚀 Quick Start

### 1. Start Infrastructure

```powershell
# From the common directory
cd d:\Github\simpleAnalytics\common
docker-compose up -d clickhouse redis
```

### 2. Build the Analytics Service

```powershell
# From Analytics directory
cd d:\Github\simpleAnalytics\Analytics
.\mvnw.cmd clean install -DskipTests
```

### 3. Run the Service

```powershell
.\mvnw.cmd spring-boot:run
```

The service will start on **http://localhost:8002**

### 4. Verify It's Running

```powershell
# Health check
curl http://localhost:8002/api/v1/health

# Or in PowerShell
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/health"
```

### 5. Access Swagger UI

Open your browser and go to:
```
http://localhost:8002/swagger-ui.html
```

## 📊 Sample Queries

### Get Overview Dashboard

```powershell
$appId = "770e8400-e29b-41d4-a716-446655440001"
$startDate = (Get-Date).AddDays(-7).ToString("yyyy-MM-ddT00:00:00")
$endDate = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")

Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/overview?startDate=$startDate&endDate=$endDate"
```

### Get Event Count

```powershell
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/count?startDate=$startDate&endDate=$endDate"
```

### Get Event Breakdown

```powershell
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/breakdown?startDate=$startDate&endDate=$endDate" | ConvertTo-Json -Depth 5
```

## 🔍 Testing with Sample Data

Make sure you have events in ClickHouse first by running the EventConsumer and Gateway services to ingest some test events.

## 🐛 Troubleshooting

### ClickHouse Connection Error
```powershell
# Verify ClickHouse is running
docker ps | Select-String clickhouse

# Test ClickHouse
curl http://localhost:8123/ping
```

### Redis Connection Error
```powershell
# Verify Redis is running
docker ps | Select-String redis

# Test Redis (requires redis-cli)
redis-cli ping
```

### Port Already in Use
If port 8002 is in use, you can change it in `application.yml`:
```yaml
server:
  port: 8003  # Change to any available port
```

## 📚 Next Steps

1. Review the full README.md for detailed API documentation
2. Explore the Swagger UI to see all available endpoints
3. Check the `/actuator/prometheus` endpoint for metrics
4. Set up Grafana dashboards for monitoring

---

**Service URL**: http://localhost:8002  
**Swagger UI**: http://localhost:8002/swagger-ui.html  
**Health Check**: http://localhost:8002/api/v1/health  
**Metrics**: http://localhost:8002/actuator/prometheus
