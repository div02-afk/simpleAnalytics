# Rate-Limited Benchmark Usage Guide

The benchmark tool now supports **rate-limited testing** with gradual ramping, detailed latency analysis, and comprehensive metrics.

## Rate Limiting Features

### 🎯 **Target RPS Control**
- Set exact requests per second rate
- Maintains consistent load over time
- Better for capacity planning and SLA validation

### 📈 **Gradual Ramping** 
- Start low and gradually increase to target RPS
- Simulates realistic traffic growth
- Helps identify breaking points

### 📊 **Enhanced Metrics**
- Detailed latency percentiles (P75, P90, P95, P99)
- Standard deviation tracking
- Time-series data collection
- RPS accuracy measurement

## Basic Rate Limiting Examples

### 1. Simple Rate-Limited Test
```bash
# 100 RPS for 5 minutes
python event-ingestion-benchmark.py --target-rps 100 --rate-duration 300

# PowerShell equivalent
.\run-benchmark.ps1 -TargetRPS 100 -RateDuration 300
```

### 2. Gradual Ramp-Up Test
```bash
# Ramp up to 200 RPS over 2 minutes, then maintain for 10 minutes
python event-ingestion-benchmark.py \
  --target-rps 200 \
  --ramp-up \
  --ramp-duration 120 \
  --rate-duration 600

# PowerShell equivalent
.\run-benchmark.ps1 -TargetRPS 200 -RampUp -RampDuration 120 -RateDuration 600
```

### 3. Single Application Load Test
```bash
# Test specific app with 50 RPS
python event-ingestion-benchmark.py \
  --target-rps 50 \
  --app-id 770e8400-e29b-41d4-a716-446655440001 \
  --single-app \
  --rate-duration 300

# PowerShell equivalent
.\run-benchmark.ps1 -TargetRPS 50 -AppId "770e8400-e29b-41d4-a716-446655440001" -SingleApp -RateDuration 300
```

## Performance Testing Scenarios

### Scenario 1: **Capacity Planning**
Find maximum sustainable RPS:

```bash
# Test different RPS levels
python event-ingestion-benchmark.py --target-rps 50 --rate-duration 180 --output capacity_50rps.json
python event-ingestion-benchmark.py --target-rps 100 --rate-duration 180 --output capacity_100rps.json
python event-ingestion-benchmark.py --target-rps 200 --rate-duration 180 --output capacity_200rps.json
python event-ingestion-benchmark.py --target-rps 500 --rate-duration 180 --output capacity_500rps.json
```

### Scenario 2: **SLA Validation**
Ensure 95% of requests complete under 100ms:

```bash
# 150 RPS sustained load
python event-ingestion-benchmark.py \
  --target-rps 150 \
  --rate-duration 600 \
  --verbose \
  --output sla_validation.json
```

### Scenario 3: **Traffic Spike Simulation**
Gradual ramp-up to peak traffic:

```bash
# Ramp from 0 to 300 RPS over 5 minutes, sustain for 15 minutes
python event-ingestion-benchmark.py \
  --target-rps 300 \
  --ramp-up \
  --ramp-duration 300 \
  --rate-duration 900 \
  --output traffic_spike.json
```

### Scenario 4: **Multi-Tenant Load**
Simulate realistic multi-tenant traffic:

```bash
# Random apps, moderate sustained load
python event-ingestion-benchmark.py \
  --target-rps 75 \
  --rate-duration 1200 \
  --use-existing-apps \
  --output multi_tenant_load.json
```

## PowerShell Examples

### Quick Rate Tests
```powershell
# 25 RPS for 2 minutes
.\run-benchmark.ps1 -TargetRPS 25 -RateDuration 120

# 100 RPS with ramp-up
.\run-benchmark.ps1 -TargetRPS 100 -RampUp -RampDuration 60 -RateDuration 300

# High load test
.\run-benchmark.ps1 -TargetRPS 250 -RateDuration 600 -OutputFile "high_load_test.json"
```

### Service Management + Rate Testing
```powershell
# Start services, run rate test, save results
.\run-benchmark.ps1 -StartServices -TargetRPS 150 -RampUp -RateDuration 300 -OutputFile "startup_test.json"
```

## Enhanced Metrics Output

Rate-limited tests provide detailed metrics:

```
=====================================EVENT INGESTION BENCHMARK RESULTS=====================================

📊 SUMMARY:
  Total Events Sent:     44,850
  Total Events Failed:   150
  Total Test Time:       300.12 seconds
  Throughput:           149.5 events/second
  Error Rate:           0.33%

🎯 RATE LIMITING:
  Target RPS:           150
  Actual RPS:           149.5
  RPS Accuracy:         99.7%

⚡ RESPONSE TIMES:
  Average:              85.3 ms
  Median:               78.1 ms
  75th Percentile:      95.2 ms
  90th Percentile:      125.4 ms
  95th Percentile:      156.8 ms
  99th Percentile:      245.7 ms
  Min:                  12.4 ms
  Max:                  892.3 ms
  Std Deviation:        45.2 ms

📈 TIME SERIES DATA:
  Data Points Collected: 300
  Avg Interval RPS:     149.8
  Avg Interval Latency: 85.1 ms

🎯 PERFORMANCE ASSESSMENT:
  ✅ Error rate is exceptional (< 0.1%)
  ✅ Average latency is excellent (< 100ms)
  ✅ P95 latency is acceptable (< 250ms)
  ✅ Throughput is good (> 100 events/s)
  ✅ Rate limiting accuracy is excellent (> 95%)
```

## Comparing Test Modes

| Feature | Traditional Load Test | Rate-Limited Test |
|---------|----------------------|------------------|
| **Control** | Concurrent requests | Requests per second |
| **Consistency** | Variable rate | Steady rate |
| **Use Case** | Max throughput | Capacity planning |
| **Metrics** | Basic percentiles | Enhanced + time series |
| **Ramping** | No | Yes (gradual) |

## Recommended RPS Targets

| Scenario | RPS Range | Duration | Purpose |
|----------|-----------|----------|---------|
| **Smoke Test** | 10-25 | 2-5 min | Basic functionality |
| **Load Test** | 50-150 | 10-30 min | Normal operations |
| **Stress Test** | 200-500 | 5-15 min | Peak capacity |
| **Endurance** | 75-100 | 1-4 hours | Stability testing |
| **Spike Test** | 10→500 (ramp) | 10-20 min | Traffic surge handling |

## Best Practices

### 1. **Start Conservative**
```bash
# Begin with low RPS to establish baseline
python event-ingestion-benchmark.py --target-rps 25 --rate-duration 120
```

### 2. **Use Ramping for Realistic Tests**
```bash
# Always ramp up for realistic traffic simulation
python event-ingestion-benchmark.py --target-rps 200 --ramp-up --ramp-duration 60
```

### 3. **Monitor Credit Consumption**
```powershell
# Run test and check Redis after
.\run-benchmark.ps1 -TargetRPS 100 -RateDuration 300
.\inspect-redis.ps1 -Pattern "credit:*" -ShowValues
```

### 4. **Save Results for Analysis**
```bash
# Always save results for comparison
python event-ingestion-benchmark.py \
  --target-rps 150 \
  --rate-duration 600 \
  --output "$(date +%Y%m%d_%H%M%S)_rate_test.json"
```

### 5. **Validate P95 Latency**
Focus on P95 rather than average latency for SLA validation - it represents the experience of 95% of your users.

## Integration with Monitoring

Use the enhanced metrics for:
- **Alerting**: Set thresholds on P95 latency and error rate
- **Capacity Planning**: Compare RPS accuracy across different loads  
- **SLA Validation**: Ensure P95 < your SLA requirements
- **Trend Analysis**: Use time series data to identify performance patterns