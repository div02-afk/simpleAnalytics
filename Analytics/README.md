# Analytics Service

Analytics API service for the Event Ingestion System. Provides real-time analytics, metrics, and insights from event data stored in ClickHouse.

## 🏗️ Architecture

The Analytics service is part of the Simple Analytics platform:

- **Gateway** (Port 8080) - Event ingestion with authentication
- **EventConsumer** (Port 8081) - Consumes events from Kafka and stores in ClickHouse
- **Analytics** (Port 8002) - **This service** - Queries ClickHouse for analytics
- **TenetService** (Port varies) - Manages tenant/application metadata

## 🚀 Features

- ✅ Real-time analytics dashboard
- ✅ Event breakdown by type and source
- ✅ Unique user and session tracking
- ✅ Time series data with custom granularity
- ✅ Context-based analytics (device, browser, OS, locale)
- ✅ Redis caching with Lettuce
- ✅ Query optimization and validation
- ✅ Swagger/OpenAPI documentation
- ✅ Prometheus metrics
- 🔄 Funnel analysis (in progress)
- 🔄 User retention analysis (in progress)

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- ClickHouse server running (via Docker Compose)
- Redis server running (via Docker Compose)

## 🔧 Configuration

### Application Configuration (`application.yml`)

```yaml
server:
  port: 8002

analytics:
  clickhouse:
    url: jdbc:clickhouse://localhost:8123/eventsdb
    username: analytics
    password: supersecret
  
  query:
    max-time-range-days: 90
    max-result-size: 10000
    default-limit: 100
  
  cache:
    enabled: true
    ttl-seconds: 300
```

## 🏃 Running the Service

### 1. Start Infrastructure

First, start ClickHouse and Redis:

```powershell
cd ..\common
docker-compose up -d clickhouse redis
```

### 2. Build the Project

```powershell
.\mvnw.cmd clean install
```

### 3. Run the Application

```powershell
.\mvnw.cmd spring-boot:run
```

Or run the JAR:

```powershell
java -jar target\Analytics-0.0.1-SNAPSHOT.jar
```

## 📡 API Endpoints

### Health Check
```http
GET /api/v1/health
```

### Dashboard Overview
```http
GET /api/v1/analytics/{appId}/overview?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59
```

Response:
```json
{
  "data": {
    "totalEvents": {
      "label": "Total Events",
      "value": 15000,
      "formattedValue": "15,000",
      "changePercentage": 12.5,
      "trend": "up"
    },
    "uniqueUsers": { ... },
    "activeSessions": { ... },
    "avgEventsPerUser": { ... },
    "eventTimeSeries": [ ... ],
    "topEvents": [ ... ],
    "sourceBreakdown": [ ... ]
  },
  "metadata": {
    "queryTime": "2025-10-08T10:30:00",
    "executionTimeMs": 245
  }
}
```

### Event Count
```http
GET /api/v1/analytics/{appId}/events/count?startDate=...&endDate=...
```

### Unique User Count
```http
GET /api/v1/analytics/{appId}/users/count?startDate=...&endDate=...
```

### Event Breakdown
```http
GET /api/v1/analytics/{appId}/events/breakdown?startDate=...&endDate=...
```

Response:
```json
{
  "data": [
    {
      "eventType": "page_view",
      "count": 8500,
      "uniqueUsers": 1200,
      "percentage": 56.7
    },
    {
      "eventType": "button_click",
      "count": 4500,
      "uniqueUsers": 950,
      "percentage": 30.0
    }
  ]
}
```

### Source Breakdown
```http
GET /api/v1/analytics/{appId}/sources/breakdown?startDate=...&endDate=...
```

### Time Series Data
```http
POST /api/v1/analytics/{appId}/timeseries
Content-Type: application/json

{
  "appId": "770e8400-e29b-41d4-a716-446655440001",
  "timeRange": {
    "startDate": "2025-10-01T00:00:00",
    "endDate": "2025-10-08T23:59:59"
  },
  "granularity": "DAY",
  "filters": [],
  "limit": 100
}
```

### Context Analytics

#### Device Breakdown
```http
GET /api/v1/analytics/{appId}/context/devices?startDate=...&endDate=...
```

#### Browser Breakdown
```http
GET /api/v1/analytics/{appId}/context/browsers?startDate=...&endDate=...
```

#### OS Breakdown
```http
GET /api/v1/analytics/{appId}/context/os?startDate=...&endDate=...
```

## 📚 API Documentation

Once the service is running, access Swagger UI at:
```
http://localhost:8002/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8002/api-docs
```

## 🔍 Monitoring

### Actuator Endpoints
```
http://localhost:8002/actuator/health
http://localhost:8002/actuator/metrics
http://localhost:8002/actuator/prometheus
```

## 🧪 Testing

### Example Test Query

Using PowerShell:
```powershell
$appId = "770e8400-e29b-41d4-a716-446655440001"
$startDate = "2025-10-01T00:00:00"
$endDate = "2025-10-08T23:59:59"

# Get overview
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/overview?startDate=$startDate&endDate=$endDate" | ConvertTo-Json -Depth 10

# Get event count
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/count?startDate=$startDate&endDate=$endDate"

# Get event breakdown
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/breakdown?startDate=$startDate&endDate=$endDate" | ConvertTo-Json -Depth 10
```

Using cURL:
```bash
# Get overview
curl "http://localhost:8002/api/v1/analytics/770e8400-e29b-41d4-a716-446655440001/overview?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"

# Get event count
curl "http://localhost:8002/api/v1/analytics/770e8400-e29b-41d4-a716-446655440001/events/count?startDate=2025-10-01T00:00:00&endDate=2025-10-08T23:59:59"
```

## 🎯 Query Features

### Time Granularity
- `HOUR` - Hourly aggregation
- `DAY` - Daily aggregation
- `WEEK` - Weekly aggregation
- `MONTH` - Monthly aggregation

### Filters (Coming Soon)
```json
{
  "filters": [
    {
      "field": "eventType",
      "operator": "EQUALS",
      "value": "page_view"
    },
    {
      "field": "source",
      "operator": "IN",
      "values": ["web", "mobile"]
    }
  ]
}
```

## 🐳 Docker Support

### Build Docker Image
```powershell
docker build -t simpleanalytics/analytics:latest .
```

### Run with Docker
```powershell
docker run -p 8002:8002 \
  -e CLICKHOUSE_URL=jdbc:clickhouse://host.docker.internal:8123/eventsdb \
  -e REDIS_HOST=host.docker.internal \
  simpleanalytics/analytics:latest
```

## 🔧 Development

### Project Structure
```
Analytics/
├── src/
│   ├── main/
│   │   ├── java/com/simpleAnalytics/Analytics/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── model/           # DTOs and entities
│   │   │   ├── exception/       # Custom exceptions
│   │   │   └── AnalyticsApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

### Key Technologies
- **Spring Boot 3.4.9**
- **ClickHouse JDBC Driver** (Official com.clickhouse)
- **Redis with Lettuce**
- **Spring Data JDBC**
- **Spring Cache**
- **Resilience4j**
- **Micrometer** (Prometheus metrics)
- **SpringDoc OpenAPI** (Swagger)

## 📊 Performance

### Caching Strategy
- Dashboard queries: 3 minutes TTL
- Regular analytics: 5 minutes TTL
- Realtime queries: 30 seconds TTL

### Query Limits
- Max time range: 90 days
- Max result size: 10,000 records
- Default page size: 100 records
- Query timeout: 60 seconds

## 🚧 Roadmap

- [ ] Complete funnel analysis implementation
- [ ] User retention cohort analysis
- [ ] Custom query builder UI
- [ ] Export capabilities (CSV, JSON, PDF)
- [ ] Real-time WebSocket updates
- [ ] Advanced filtering with query DSL
- [ ] Materialized views for performance
- [ ] A/B testing analytics
- [ ] Predictive analytics with ML

## 🤝 Integration

This service integrates with:
- **Gateway**: Receives app IDs for analytics
- **EventConsumer**: Reads events from ClickHouse
- **Redis**: Caches query results
- **ClickHouse**: Primary data source

## 📝 Notes

- Authentication will be added via API Gateway later
- Currently no direct communication with TenetService
- All queries use prepared statements to prevent SQL injection
- Field names are sanitized and validated

## 🐛 Troubleshooting

### Connection Issues
```yaml
# Check ClickHouse
docker ps | grep clickhouse

# Test ClickHouse connection
curl http://localhost:8123/ping

# Check Redis
docker ps | grep redis
redis-cli ping
```

### Build Issues
```powershell
# Clean and rebuild
.\mvnw.cmd clean install -U

# Skip tests if needed
.\mvnw.cmd clean install -DskipTests
```

## 📄 License

Part of the Simple Analytics Event Ingestion System.

---

**Service Status**: ✅ Development Ready  
**Version**: 0.0.1-SNAPSHOT  
**Port**: 8002  
**Health Check**: http://localhost:8002/api/v1/health
