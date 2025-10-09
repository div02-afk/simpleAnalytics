# Configuration Fix - ClickHouse DataSource Issue

## Problem
```
Error creating bean with name 'clickHouseDataSource': 
jdbcUrl is required with driverClassName.
```

## Root Cause
The issue was caused by conflicting Spring DataSource configurations:

1. **application.properties** had Spring Boot's default datasource properties (`spring.datasource.*`)
2. **application.yml** had custom HikariCP properties (`analytics.clickhouse.*`)
3. Spring Boot was trying to auto-configure a datasource from `application.properties`
4. Our custom `@ConfigurationProperties` wasn't properly mapping to HikariCP's expected property names

## Fix Applied

### 1. Updated application.yml
Changed property names to match HikariCP's expected format:
- `url` → `jdbc-url` (HikariCP expects `jdbcUrl`)
- Added all HikariCP properties at the root level
- Added `spring.sql.init.mode: never` to disable SQL initialization

```yaml
analytics:
  clickhouse:
    jdbc-url: jdbc:clickhouse://localhost:8123/eventsdb
    username: analytics
    password: supersecret
    driver-class-name: com.clickhouse.jdbc.ClickHouseDriver
    pool-name: ClickHousePool
    maximum-pool-size: 20
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
    validation-timeout: 5000
    auto-commit: true
    connection-test-query: SELECT 1
    leak-detection-threshold: 60000
```

### 2. Cleaned up application.properties
Removed conflicting datasource configuration:
```properties
# Before (WRONG - causes conflicts)
spring.datasource.url=jdbc:clickhouse:http://localhost:8123/eventsdb
spring.datasource.username=analytics
spring.datasource.password=supersecret
spring.datasource.driver-class-name=com.clickhouse.jdbc.ClickHouseDriver

# After (CORRECT)
spring.application.name=Analytics
```

### 3. Simplified ClickHouseConfig.java
```java
@Bean
@ConfigurationProperties(prefix = "analytics.clickhouse")
public HikariConfig clickHouseHikariConfig() {
    // @ConfigurationProperties will automatically map YAML properties
    // jdbc-url → setJdbcUrl, username → setUsername, etc.
    return new HikariConfig();
}
```

## How @ConfigurationProperties Works

When you use `@ConfigurationProperties(prefix = "analytics.clickhouse")`:

**YAML Property** → **Java Method Called**
- `jdbc-url` → `setJdbcUrl(String)`
- `username` → `setUsername(String)`
- `password` → `setPassword(String)`
- `driver-class-name` → `setDriverClassName(String)`
- `maximum-pool-size` → `setMaximumPoolSize(int)`
- `connection-timeout` → `setConnectionTimeout(long)`

Spring Boot automatically:
1. Converts kebab-case to camelCase
2. Calls the appropriate setter methods
3. Handles type conversion

## Verification

After applying these changes, the application should start successfully:

```powershell
cd d:\Github\simpleAnalytics\Analytics
.\mvnw.cmd spring-boot:run
```

Expected output:
```
Initializing ClickHouse DataSource with URL: jdbc:clickhouse://localhost:8123/eventsdb
ClickHouse JdbcTemplate initialized successfully
Started AnalyticsApplication in X.XXX seconds
```

## Testing the Fix

1. **Check health endpoint:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8002/api/v1/health"
```

2. **Verify ClickHouse connection:**
```powershell
# Make sure ClickHouse is running
docker ps | Select-String clickhouse

# Test a simple query (once app is running)
$appId = "770e8400-e29b-41d4-a716-446655440001"
$startDate = "2025-10-01T00:00:00"
$endDate = "2025-10-08T23:59:59"

Invoke-RestMethod -Uri "http://localhost:8002/api/v1/analytics/$appId/events/count?startDate=$startDate&endDate=$endDate"
```

## Important Notes

### Property Name Mapping Rules

HikariCP expects these exact property names:
- ✅ `jdbc-url` or `jdbcUrl` → **YES**
- ❌ `url` → **NO** (Spring's datasource property, not HikariCP's)

### Common Pitfalls

1. **Don't mix property files**: Use either `application.properties` OR `application.yml`, not both for the same properties
2. **Kebab-case vs camelCase**: YAML prefers kebab-case (`jdbc-url`), but Spring converts it to camelCase for setters
3. **HikariCP vs Spring Boot**: HikariCP has its own property names that differ from Spring Boot's defaults

### Alternative Approaches

If you prefer to keep properties in a custom format, you can manually configure:

```java
@Bean
public HikariConfig clickHouseHikariConfig(AnalyticsProperties properties) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(properties.getClickhouse().getUrl());
    config.setUsername(properties.getClickhouse().getUsername());
    config.setPassword(properties.getClickhouse().getPassword());
    config.setDriverClassName(properties.getClickhouse().getDriverClassName());
    // ... set other properties
    return config;
}
```

But using `@ConfigurationProperties` directly on HikariConfig is simpler and less error-prone.

## Summary

✅ **Fixed**: Changed `url` to `jdbc-url` in application.yml  
✅ **Fixed**: Removed conflicting datasource config from application.properties  
✅ **Fixed**: Added `spring.sql.init.mode: never` to disable SQL initialization  
✅ **Fixed**: Simplified HikariConfig bean creation  

The application should now start successfully and connect to ClickHouse! 🎉
