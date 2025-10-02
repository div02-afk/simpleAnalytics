# Event Ingestion Benchmark Runner for Windows PowerShell
# Simple Analytics Event Ingestion Performance Testing

param(
    [string]$GatewayUrl = "http://localhost:8001",
    [int]$Events = 1000,
    [int]$Concurrent = 50,
    [int]$BatchSize = 10,
    [int]$Duration = 0,
    [string]$AppId = "",  # Will use random existing app by default
    [string]$AuthToken = [System.Guid]::NewGuid(),
    [string]$OutputFile = "",
    [switch]$Verbose,
    [switch]$StartServices,
    [switch]$StopServices,
    [switch]$SingleApp,     # Use only specified AppId
    [switch]$ListApps,      # List available app IDs
    # Rate limiting options
    [int]$TargetRPS = 0,    # Target requests per second
    [switch]$RampUp,        # Gradually ramp up to target RPS
    [int]$RampDuration = 60, # Ramp up duration in seconds
    [int]$RateDuration = 300 # Rate-limited test duration in seconds
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
        [bool]$VerboseOutput,
        # Rate limiting parameters
        [int]$TargetRPS,
        [bool]$RampUp,
        [int]$RampDuration,
        [int]$RateDuration
    )
    
    Write-Info "Starting event ingestion benchmark..."
    Write-Info "Configuration:"
    Write-Host "  Gateway URL: $Url"
    
    # Display test mode and parameters
    if ($TargetRPS -gt 0) {
        Write-Host "  Mode: Rate Limited ($TargetRPS RPS)" -ForegroundColor Cyan
        if ($RampUp) {
            Write-Host "  Ramp Up: $RampDuration seconds to reach target" -ForegroundColor Cyan
        }
        Write-Host "  Test Duration: $RateDuration seconds" -ForegroundColor Cyan
    } else {
        Write-Host "  Mode: Traditional Load Test" -ForegroundColor Cyan
        if ($TestDuration -gt 0) {
            Write-Host "  Test Duration: $TestDuration seconds"
        } else {
            Write-Host "  Total Events: $EventCount"
        }
        Write-Host "  Concurrent Requests: $ConcurrentRequests"
        Write-Host "  Batch Size: $BatchSize"
    }
    
    Write-Host "  App ID: $ApplicationId"
    Write-Host ""
    
    $arguments = @(
        "$PSScriptRoot\event-ingestion-benchmark.py",
        "--url", $Url,
        "--auth-token", $Authorization
    )
    
    # Handle app ID arguments
    if ($ApplicationId) {
        $arguments += "--app-id", $ApplicationId
        if ($SingleApp) {
            $arguments += "--single-app"
        }
    }
    
    # Rate limiting vs traditional mode
    if ($TargetRPS -gt 0) {
        $arguments += "--target-rps", $TargetRPS
        $arguments += "--rate-duration", $RateDuration
        
        if ($RampUp) {
            $arguments += "--ramp-up"
            $arguments += "--ramp-duration", $RampDuration
        }
    } else {
        # Traditional mode parameters
        $arguments += "--concurrent", $ConcurrentRequests
        $arguments += "--batch-size", $BatchSize
        
        if ($TestDuration -gt 0) {
            $arguments += "--duration", $TestDuration
        } else {
            $arguments += "--events", $EventCount
        }
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

# Handle list apps command
if ($ListApps) {
    Write-Info "Listing available application IDs..."
    try {
        & python "$PSScriptRoot\event-ingestion-benchmark.py" --list-apps
        exit 0
    } catch {
        Write-Error "Failed to list applications: $_"
        exit 1
    }
}

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
$benchmarkArgs = @{
    Url = $GatewayUrl
    Authorization = $AuthToken
    Output = $OutputFile
    VerboseOutput = $Verbose
    # Rate limiting parameters
    TargetRPS = $TargetRPS
    RampUp = $RampUp.IsPresent
    RampDuration = $RampDuration
    RateDuration = $RateDuration
}

# Traditional mode parameters (only used if not rate limited)
if ($TargetRPS -eq 0) {
    $benchmarkArgs.ConcurrentRequests = $Concurrent
    $benchmarkArgs.BatchSize = $BatchSize
}

# Handle AppId - use existing apps by default unless SingleApp is specified
if ($AppId -and $SingleApp) {
    $benchmarkArgs.ApplicationId = $AppId
    Write-Info "Using single application ID: $AppId"
} elseif ($AppId) {
    $benchmarkArgs.ApplicationId = $AppId
    if ($TargetRPS -eq 0) {
        Write-Info "Starting with app ID: $AppId (will use random existing apps)"
    } else {
        Write-Info "Rate-limited test with app ID: $AppId"
    }
} else {
    $benchmarkArgs.ApplicationId = ""  # Let Python script choose random existing app
    if ($TargetRPS -eq 0) {
        Write-Info "Using random existing application IDs from database"
    } else {
        Write-Info "Rate-limited test using random existing application IDs"
    }
}

# Set duration/events based on mode
if ($TargetRPS -gt 0) {
    # Rate-limited mode - duration is handled by RateDuration parameter
    Write-Info "Rate-limited mode: $TargetRPS RPS for $RateDuration seconds"
    if ($RampUp) {
        Write-Info "Ramping up over $RampDuration seconds"
    }
} elseif ($Duration -gt 0) {
    $benchmarkArgs.TestDuration = $Duration
    Write-Info "Duration-based test: $Duration seconds"
} else {
    $benchmarkArgs.EventCount = $Events
    Write-Info "Event count-based test: $Events events"
}

Start-Benchmark @benchmarkArgs

Write-Host ""
Write-Info "Benchmark completed. Check the results above."
if ($OutputFile) {
    Write-Info "Detailed results saved to: $OutputFile"
}