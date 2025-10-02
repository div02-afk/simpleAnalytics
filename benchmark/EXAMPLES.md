# Benchmark Usage Examples

The benchmark script has been updated to use existing application IDs from your database for more realistic testing.

## Quick Start Examples

### 1. List Available Applications
```bash
# See all application IDs from your database
python event-ingestion-benchmark.py --list-apps
```

### 2. Basic Test (Uses Random Existing Apps)
```bash
# Default: Random existing app IDs, 10,000 events, 100 concurrent
python event-ingestion-benchmark.py

# PowerShell equivalent
.\run-benchmark.ps1
```

### 3. Test Single Application
```bash
# Use specific app ID only
python event-ingestion-benchmark.py --app-id 770e8400-e29b-41d4-a716-446655440001 --single-app

# PowerShell equivalent  
.\run-benchmark.ps1 -AppId "770e8400-e29b-41d4-a716-446655440001" -SingleApp
```

### 4. High Load Test with Random Apps
```bash
# 50,000 events across multiple existing apps
python event-ingestion-benchmark.py --events 50000 --concurrent 200

# PowerShell equivalent
.\run-benchmark.ps1 -Events 50000 -Concurrent 200
```

### 5. Duration-Based Test
```bash
# Run for 5 minutes with random existing apps
python event-ingestion-benchmark.py --duration 300 --concurrent 100

# PowerShell equivalent
.\run-benchmark.ps1 -Duration 300 -Concurrent 100
```

## Application IDs Available

The benchmark includes these existing applications from your database:

| Application | ID | Company |
|-------------|----|---------| 
| Acme Web Analytics | `770e8400-e29b-41d4-a716-446655440001` | Acme Corporation |
| Acme Mobile App | `770e8400-e29b-41d4-a716-446655440002` | Acme Corporation |
| TechStart Dashboard | `770e8400-e29b-41d4-a716-446655440004` | TechStart Inc |
| Global Web Platform | `770e8400-e29b-41d4-a716-446655440006` | Global Analytics Ltd |
| Enterprise Portal | `770e8400-e29b-41d4-a716-446655440011` | Enterprise Solutions |
| ... (17 total applications) | | |

## Test Scenarios

### Scenario 1: Load Testing Specific App
Test credit consumption for a specific application:
```bash
python event-ingestion-benchmark.py \
  --app-id 770e8400-e29b-41d4-a716-446655440001 \
  --single-app \
  --events 10000 \
  --output acme_web_load_test.json
```

### Scenario 2: Multi-Tenant Load Testing  
Simulate realistic multi-tenant traffic:
```bash
python event-ingestion-benchmark.py \
  --events 100000 \
  --concurrent 150 \
  --batch-size 20 \
  --output multi_tenant_test.json
```

### Scenario 3: Stress Testing
Find system limits:
```bash
python event-ingestion-benchmark.py \
  --duration 600 \
  --concurrent 500 \
  --batch-size 50 \
  --verbose \
  --output stress_test.json
```

## PowerShell Examples

### List Apps
```powershell
.\run-benchmark.ps1 -ListApps
```

### Quick Load Test
```powershell
.\run-benchmark.ps1 -Events 5000 -Concurrent 75
```

### Specific App Test
```powershell
.\run-benchmark.ps1 -AppId "770e8400-e29b-41d4-a716-446655440006" -SingleApp -Events 20000
```

### With Service Management
```powershell
# Start services, run test, and optionally stop
.\run-benchmark.ps1 -StartServices -Events 10000 -OutputFile "test_results.json"
```

## Output Examples

The benchmark now shows which apps are being used:
```
🚀 Starting benchmark with configuration:
   App Strategy: Random existing apps
   App Pool: 17 existing applications

📊 SUMMARY:
  Total Events Sent:     9,856
  Total Events Failed:   144
  Throughput:           247.3 events/second
```

## Tips

1. **Use `--list-apps`** to see all available application IDs
2. **Random apps (default)** simulates realistic multi-tenant load
3. **Single app mode** tests specific application limits
4. **Monitor credit usage** in Redis after tests
5. **Check database** for credit consumption patterns