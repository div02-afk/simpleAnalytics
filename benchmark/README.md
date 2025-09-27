# Event Ingestion Benchmark Suite

Comprehensive benchmarking tools for testing the Simple Analytics event ingestion pipeline performance.

## 🚀 Quick Start

### Prerequisites

- Python 3.7+
- Docker and Docker Compose
- PowerShell (for Windows users)

### Installation

1. Install Python dependencies:
```bash
pip install aiohttp asyncio
```

2. Start the services:
```bash
# Start Docker services
cd ../common
docker-compose up -d

# Wait for services to be ready (30-60 seconds)
```

### Running Benchmarks

#### Using Python Script (Cross-platform)

```bash
# Basic benchmark with 1000 events
python event-ingestion-benchmark.py

# Custom configuration
python event-ingestion-benchmark.py --events 5000 --concurrent 100 --batch-size 20

# Duration-based test (run for 5 minutes)
python event-ingestion-benchmark.py --duration 300 --concurrent 75

# Save results to file
python event-ingestion-benchmark.py --events 1000 --output results.json
```

#### Using PowerShell Script (Windows)

```powershell
# Basic benchmark
.\run-benchmark.ps1

# Start services and run benchmark
.\run-benchmark.ps1 -StartServices -Events 1000 -Concurrent 50

# Custom configuration
.\run-benchmark.ps1 -Events 5000 -Concurrent 100 -BatchSize 20 -OutputFile "results.json"

# Duration-based test
.\run-benchmark.ps1 -Duration 300 -Concurrent 75

# Stop services after testing
.\run-benchmark.ps1 -StopServices
```

## 📊 Benchmark Types

### 1. Quick Test
Fast validation test with minimal load:
```bash
python event-ingestion-benchmark.py --events 100 --concurrent 10 --batch-size 5
```

### 2. Load Test
Normal operational load simulation:
```bash
python event-ingestion-benchmark.py --events 10000 --concurrent 100 --batch-size 20
```

### 3. Stress Test
High-volume stress testing:
```bash
python event-ingestion-benchmark.py --events 50000 --concurrent 200 --batch-size 50
```

### 4. Spike Test
Sudden traffic spike simulation:
```bash
python event-ingestion-benchmark.py --events 5000 --concurrent 500 --batch-size 100
```

### 5. Endurance Test
Long-running stability test:
```bash
python event-ingestion-benchmark.py --duration 3600 --concurrent 75 --batch-size 15
```

## 📈 Understanding Results

### Key Metrics

- **Throughput**: Events processed per second
- **Response Time**: Time taken for each request
- **Error Rate**: Percentage of failed requests
- **Percentiles**: Distribution of response times

### Performance Benchmarks

| Metric | Excellent | Good | Poor |
|--------|-----------|------|------|
| Throughput | >100 events/s | >50 events/s | <50 events/s |
| Avg Response Time | <100ms | <500ms | >500ms |
| Error Rate | <1% | <5% | >5% |
| P95 Response Time | <200ms | <1000ms | >1000ms |

### Sample Output

```
============================================================
           EVENT INGESTION BENCHMARK RESULTS
============================================================

📊 SUMMARY:
  Total Events Sent:     995
  Total Events Failed:   5
  Total Test Time:       12.34 seconds
  Throughput:           80.61 events/second
  Error Rate:           0.50%

⚡ RESPONSE TIMES:
  Average:              124.56 ms
  Median:               98.23 ms
  95th Percentile:      256.78 ms
  99th Percentile:      389.45 ms
  Min:                  45.12 ms
  Max:                  567.89 ms

🎯 PERFORMANCE ASSESSMENT:
  ✅ Error rate is excellent (< 1%)
  ⚠️  Average response time is acceptable (< 500ms)
  ✅ Throughput is excellent (> 100 events/s)
============================================================
```

## 🔧 Configuration Options

### Command Line Arguments

| Argument | Default | Description |
|----------|---------|-------------|
| `--url` | `http://localhost:8080` | Gateway URL |
| `--events` | `1000` | Total number of events |
| `--concurrent` | `50` | Concurrent requests |
| `--batch-size` | `10` | Batch size for requests |
| `--duration` | None | Test duration in seconds |
| `--app-id` | Generated UUID | Application ID |
| `--auth-token` | Generated UUID | Authorization token |
| `--output` | None | Output file for results |
| `--verbose` | False | Enable verbose logging |

### Configuration File

Use `benchmark-config.json` for predefined test scenarios:

```json
{
  "load_test": {
    "gateway_url": "http://localhost:8080",
    "total_events": 10000,
    "concurrent_requests": 100,
    "batch_size": 20
  }
}
```

## 🏗️ Event Data Structure

The benchmark generates realistic event data:

```json
{
  "appId": "uuid",
  "anonymousId": "uuid",
  "sessionId": "uuid",
  "userId": "uuid",
  "timestamp": "2025-09-17T10:30:00Z",
  "eventType": "page_view",
  "source": "web",
  "metadata": {
    "page_url": "https://example.com/products",
    "referrer": "https://google.com",
    "user_agent": "Mozilla/5.0...",
    "device_type": "Desktop",
    "browser": "Chrome",
    "os": "Windows"
  }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure Gateway is running on the specified port
   - Check Docker services are up: `docker-compose ps`

2. **High Error Rates**
   - Reduce concurrent requests
   - Check system resources (CPU, Memory)
   - Verify Kafka and database connectivity

3. **Slow Response Times**
   - Check database performance
   - Monitor Kafka consumer lag
   - Verify network connectivity

4. **Python Dependencies**
   ```bash
   pip install aiohttp asyncio
   ```

### Health Checks

```bash
# Check Gateway health
curl http://localhost:8080/actuator/health

# Check EventConsumer health
curl http://localhost:8000/api/health/events

# Check Docker services
docker-compose ps
```

## 📁 File Structure

```
benchmark/
├── event-ingestion-benchmark.py    # Main Python benchmark script (cross-platform)
├── run-benchmark.ps1               # PowerShell wrapper script (Windows)
├── run-benchmark.sh                # Bash wrapper script (Linux/macOS)
├── run-benchmark.bat               # Batch wrapper script (Windows CMD)
├── example-runner.py               # Interactive example runner
├── benchmark-config.json           # Configuration presets
├── README.md                       # This file
└── results/                        # Output directory (created automatically)
    ├── benchmark-2025-09-17-10-30.json
    └── ...
```

## 🎮 Interactive Example Runner

For a guided experience, use the interactive example runner:

```bash
python example-runner.py
```

This will present you with various pre-configured benchmark scenarios:
- Quick validation test (100 events)
- Standard load test (1000 events)
- High concurrency test (2000 events with 100 concurrent)
- Duration-based test (60 seconds)

## 🤝 Contributing

To add new benchmark scenarios or improve the existing ones:

1. Update `benchmark-config.json` for new presets
2. Modify the `EventGenerator` class for different event types
3. Add new metrics in the `BenchmarkResult` class
4. Update documentation

## 📝 License

Same as the main Simple Analytics project.