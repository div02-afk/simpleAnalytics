# 🚀 Event Ingestion Benchmark Suite - Quick Start Guide

## What's Been Created

I've created a comprehensive benchmarking suite for your Simple Analytics event ingestion pipeline with the following components:

### 📁 Files Created

1. **`event-ingestion-benchmark.py`** - Main Python benchmark script (cross-platform)
2. **`run-benchmark.ps1`** - PowerShell wrapper script (Windows)
3. **`run-benchmark.sh`** - Bash wrapper script (Linux/macOS)
4. **`run-benchmark.bat`** - Batch wrapper script (Windows CMD)
5. **`example-runner.py`** - Interactive example runner
6. **`benchmark-config.json`** - Configuration presets for different test scenarios
7. **`README.md`** - Comprehensive documentation

## 🎯 Key Features

### Realistic Event Generation
- Generates authentic event data matching your protobuf schema
- Includes realistic metadata (user agents, browsers, device types, etc.)
- Pool of users and sessions for realistic patterns

### Comprehensive Metrics
- **Throughput**: Events per second
- **Response Times**: Average, median, P95, P99, min, max
- **Error Analysis**: Detailed error reporting and categorization
- **Performance Assessment**: Automatic evaluation of results

### Multiple Test Scenarios
- **Quick Test**: 100 events for validation
- **Load Test**: 10,000 events for normal load simulation
- **Stress Test**: 50,000 events for high-volume testing
- **Spike Test**: Sudden traffic bursts with high concurrency
- **Endurance Test**: Long-running stability testing

### Cross-Platform Support
- Python script works on Windows, macOS, and Linux
- Platform-specific wrapper scripts for ease of use
- Color-coded output for better readability

## 🏃 Quick Usage Examples

### Basic Test (1000 events)
```bash
python event-ingestion-benchmark.py
```

### High Load Test
```bash
python event-ingestion-benchmark.py --events 10000 --concurrent 100 --batch-size 20
```

### Duration-Based Test (5 minutes)
```bash
python event-ingestion-benchmark.py --duration 300 --concurrent 75
```

### Save Results to File
```bash
python event-ingestion-benchmark.py --events 1000 --output results.json --verbose
```

### Interactive Mode
```bash
python example-runner.py
```

## 📊 Sample Output

The benchmark provides detailed performance analysis:

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
```

## 🔧 Prerequisites

1. **Python 3.7+** with `aiohttp` and `asyncio` packages
2. **Docker and Docker Compose** for running the services
3. **Running Services**: Gateway (port 8080), EventConsumer (port 8000), Kafka (port 9092)

## 🚀 Getting Started

1. **Start the services:**
   ```bash
   cd ../common
   docker-compose up -d
   ```

2. **Install Python dependencies:**
   ```bash
   pip install aiohttp asyncio
   ```

3. **Run a quick test:**
   ```bash
   python event-ingestion-benchmark.py --events 100 --verbose
   ```

## 🎮 Advanced Usage

### PowerShell (Windows)
```powershell
.\run-benchmark.ps1 -StartServices -Events 5000 -Concurrent 100 -OutputFile "results.json"
```

### Bash (Linux/macOS)
```bash
./run-benchmark.sh --start-services --events 5000 --concurrent 100 --output results.json
```

### With Configuration Presets
The `benchmark-config.json` file contains predefined scenarios for different testing needs.

## 🎯 Performance Benchmarks

| Metric | Excellent | Good | Poor |
|--------|-----------|------|------|
| Throughput | >100 events/s | >50 events/s | <50 events/s |
| Avg Response Time | <100ms | <500ms | >500ms |
| Error Rate | <1% | <5% | >5% |

## 🔍 What It Tests

- **HTTP Request Performance**: Measures actual API response times
- **Concurrent Load Handling**: Tests multiple simultaneous requests
- **Error Handling**: Identifies connection issues, timeouts, server errors
- **Throughput Capacity**: Determines maximum events per second
- **System Stability**: Long-running tests for reliability assessment

## 📈 Next Steps

1. **Start Services**: Use Docker Compose to start your analytics stack
2. **Run Quick Test**: Validate everything works with a small test
3. **Load Testing**: Run larger tests to understand system capacity
4. **Performance Tuning**: Use results to optimize your system
5. **Monitoring**: Set up regular benchmarks for performance regression testing

The benchmark suite is ready to use and will help you understand your event ingestion pipeline's performance characteristics under various load conditions!