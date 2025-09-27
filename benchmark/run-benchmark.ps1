# Event Ingestion Benchmark Runner for Windows PowerShell
# Simple Analytics Event Ingestion Performance Testing

param(
    [string]$GatewayUrl = "http://localhost:8080",
    [int]$Events = 1000,
    [int]$Concurrent = 50,
    [int]$BatchSize = 10,
    [int]$Duration = 0,
    [string]$AppId = [System.Guid]::NewGuid(),
    [string]$AuthToken = [System.Guid]::NewGuid(),
    [string]$OutputFile = "",
    [switch]$Verbose,
    [switch]$StartServices,
    [switch]$StopServices
)

# Color output functions
function Write-Success { param($Message) Write-Host "[+] $Message" -ForegroundColor Green }
function Write-Warning { param($Message) Write-Host "[!] $Message" -ForegroundColor Yellow }
function Write-Error { param($Message) Write-Host "[x] $Message" -ForegroundColor Red }
function Write-Info { param($Message) Write-Host "[i] $Message" -ForegroundColor Cyan }

# Check if Python is available
function Test-PythonAvailable {
    try {
        $pythonVersion = python --version 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Python found: $pythonVersion"
            return $true
        }
    } catch {
        Write-Error "Python is not available. Please install Python 3.7+ to run benchmarks."
        return $false
    }
    return $false
}

# Install Python dependencies
function Install-Dependencies {
    Write-Info "Installing Python dependencies..."
    try {
        python -m pip install aiohttp asyncio --quiet
        Write-Success "Dependencies installed successfully"
        return $true
    } catch {
        Write-Error "Failed to install dependencies: $_"
        return $false
    }
}

# Start Docker services
function Start-Services {
    Write-Info "Starting Docker services..."
    try {
        Push-Location "$PSScriptRoot\..\common"
        docker-compose up -d
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker services started"
            Write-Info "Waiting 30 seconds for services to be ready..."
            Start-Sleep -Seconds 30
            return $true
        } else {
            Write-Error "Failed to start Docker services"
            return $false
        }
    } catch {
        Write-Error "Error starting services: $_"
        return $false
    } finally {
        Pop-Location
    }
}

# Stop Docker services
function Stop-Services {
    Write-Info "Stopping Docker services..."
    try {
        Push-Location "$PSScriptRoot\..\common"
        docker-compose down
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker services stopped"
            return $true
        } else {
            Write-Error "Failed to stop Docker services"
            return $false
        }
    } catch {
        Write-Error "Error stopping services: $_"
        return $false
    } finally {
        Pop-Location
    }
}

# Check if services are running
function Test-ServicesRunning {
    Write-Info "Checking if services are running..."
    
    # Check Gateway
    try {
        $response = Invoke-WebRequest -Uri "$GatewayUrl/actuator/health" -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Success "Gateway is running at $GatewayUrl"
        } else {
            Write-Warning "Gateway responded with status $($response.StatusCode)"
        }
    } catch {
        Write-Warning "Gateway health check failed: $_"
    }
    
    # Check EventConsumer
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8000/api/health/events" -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Success "EventConsumer is running"
        } else {
            Write-Warning "EventConsumer responded with status $($response.StatusCode)"
        }
    } catch {
        Write-Warning "EventConsumer health check failed: $_"
    }
    
    # Check Kafka
    try {
        $kafkaResponse = Test-NetConnection -ComputerName localhost -Port 9092 -WarningAction SilentlyContinue
        if ($kafkaResponse.TcpTestSucceeded) {
            Write-Success "Kafka is running on port 9092"
        } else {
            Write-Warning "Kafka is not accessible on port 9092"
        }
    } catch {
        Write-Warning "Kafka connectivity check failed: $_"
    }
}

# Run the benchmark
function Start-Benchmark {
    param(
        [string]$Url,
        [int]$EventCount,
        [int]$ConcurrentRequests,
        [int]$BatchSize,
        [int]$TestDuration,
        [string]$ApplicationId,
        [string]$Authorization,
        [string]$Output,
        [bool]$VerboseOutput
    )
    
    Write-Info "Starting event ingestion benchmark..."
    Write-Info "Configuration:"
    Write-Host "  Gateway URL: $Url"
    if ($TestDuration -gt 0) {
        Write-Host "  Test Duration: $TestDuration seconds"
    } else {
        Write-Host "  Total Events: $EventCount"
    }
    Write-Host "  Concurrent Requests: $ConcurrentRequests"
    Write-Host "  Batch Size: $BatchSize"
    Write-Host "  App ID: $ApplicationId"
    Write-Host ""
    
    $arguments = @(
        "$PSScriptRoot\event-ingestion-benchmark.py",
        "--url", $Url,
        "--concurrent", $ConcurrentRequests,
        "--batch-size", $BatchSize,
        "--app-id", $ApplicationId,
        "--auth-token", $Authorization
    )
    
    if ($TestDuration -gt 0) {
        $arguments += "--duration", $TestDuration
    } else {
        $arguments += "--events", $EventCount
    }
    
    if ($Output) {
        $arguments += "--output", $Output
    }
    
    if ($VerboseOutput) {
        $arguments += "--verbose"
    }
    
    try {
        & python @arguments
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Benchmark completed successfully"
        } else {
            Write-Error "Benchmark failed with exit code $LASTEXITCODE"
        }
    } catch {
        Write-Error "Error running benchmark: $_"
    }
}

# Main execution
Write-Host "="*60 -ForegroundColor Blue
Write-Host "    SIMPLE ANALYTICS EVENT INGESTION BENCHMARK" -ForegroundColor Blue
Write-Host "="*60 -ForegroundColor Blue
Write-Host ""

# Handle service management
if ($StopServices) {
    Stop-Services
    exit 0
}

if ($StartServices) {
    if (-not (Start-Services)) {
        Write-Error "Failed to start services. Exiting."
        exit 1
    }
}

# Check prerequisites
if (-not (Test-PythonAvailable)) {
    exit 1
}

# Install dependencies
if (-not (Install-Dependencies)) {
    exit 1
}

# Check services
Test-ServicesRunning

# Run benchmark
if ($Duration -gt 0) {
    Start-Benchmark -Url $GatewayUrl -TestDuration $Duration -ConcurrentRequests $Concurrent -BatchSize $BatchSize -ApplicationId $AppId -Authorization $AuthToken -Output $OutputFile -VerboseOutput $Verbose
} else {
    Start-Benchmark -Url $GatewayUrl -EventCount $Events -ConcurrentRequests $Concurrent -BatchSize $BatchSize -ApplicationId $AppId -Authorization $AuthToken -Output $OutputFile -VerboseOutput $Verbose
}

Write-Host ""
Write-Info "Benchmark completed. Check the results above."
if ($OutputFile) {
    Write-Info "Detailed results saved to: $OutputFile"
}