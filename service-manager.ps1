# Simple Analytics Service Manager
# PowerShell script to manage all services easily

param(
    [Parameter(Position=0)]
    [ValidateSet("start", "stop", "restart", "status", "build", "test", "clean", "help")]
    [string]$Action = "help",
    
    [ValidateSet("all", "docker", "gateway", "eventconsumer", "tenetservice")]
    [string]$Service = "all",
    
    [switch]$Dev,
    [switch]$Prod,
    [switch]$VerboseOutput
)

# Color output functions
function Write-Success { param($Message) Write-Host "[+] $Message" -ForegroundColor Green }
function Write-Warning { param($Message) Write-Host "[!] $Message" -ForegroundColor Yellow }
function Write-Error { param($Message) Write-Host "[x] $Message" -ForegroundColor Red }
function Write-Info { param($Message) Write-Host "[i] $Message" -ForegroundColor Cyan }

function Show-Help {
    Write-Host "Simple Analytics Service Manager" -ForegroundColor Blue
    Write-Host "=================================" -ForegroundColor Blue
    Write-Host ""
    Write-Host "Usage: .\service-manager.ps1 <action> [options]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Actions:" -ForegroundColor Green
    Write-Host "  start     - Start services"
    Write-Host "  stop      - Stop services"  
    Write-Host "  restart   - Restart services"
    Write-Host "  status    - Check service status"
    Write-Host "  build     - Build all services"
    Write-Host "  test      - Run tests"
    Write-Host "  clean     - Clean build artifacts"
    Write-Host "  help      - Show this help"
    Write-Host ""
    Write-Host "Services:" -ForegroundColor Green
    Write-Host "  all           - All services (default)"
    Write-Host "  docker        - Docker services only"
    Write-Host "  gateway       - Gateway service only"
    Write-Host "  eventconsumer - EventConsumer service only" 
    Write-Host "  tenetservice  - TenetService only"
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Green
    Write-Host "  -Dev      - Use development profile"
    Write-Host "  -Prod     - Use production profile"
    Write-Host "  -VerboseOutput - Verbose output"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  .\service-manager.ps1 start docker"
    Write-Host "  .\service-manager.ps1 start gateway -Dev"
    Write-Host "  .\service-manager.ps1 build"
    Write-Host "  .\service-manager.ps1 status"
}

function Test-Port {
    param([int]$Port)
    $connection = Test-NetConnection -ComputerName "localhost" -Port $Port -WarningAction SilentlyContinue
    return $connection.TcpTestSucceeded
}

function Get-ServiceStatus {
    Write-Info "Checking service status..."
    
    # Check Docker services
    Write-Host ""
    Write-Host "Docker Services:" -ForegroundColor Yellow
    try {
        $kafkaStatus = Test-Port -Port 29092
        $clickhouseStatus = Test-Port -Port 8123
        $redisStatus = Test-Port -Port 6379
        $schemaRegistryStatus = Test-Port -Port 8081
        
        Write-Host "  Kafka (29092):          " -NoNewline
        if ($kafkaStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
        
        Write-Host "  ClickHouse (8123):     " -NoNewline  
        if ($clickhouseStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
        
        Write-Host "  Redis (6379):          " -NoNewline
        if ($redisStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
        
        Write-Host "  Schema Registry (8081): " -NoNewline
        if ($schemaRegistryStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
    } catch {
        Write-Error "Error checking Docker services: $_"
    }
    
    # Check Application services
    Write-Host ""
    Write-Host "Application Services:" -ForegroundColor Yellow
    
    $gatewayStatus = Test-Port -Port 8001
    $eventConsumerStatus = Test-Port -Port 8000
    $tenetServiceStatus = Test-Port -Port 8080
    
    Write-Host "  Gateway (8001):        " -NoNewline
    if ($gatewayStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
    
    Write-Host "  EventConsumer (8000):  " -NoNewline
    if ($eventConsumerStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
    
    Write-Host "  TenetService (8080):   " -NoNewline
    if ($tenetServiceStatus) { Write-Success "Running" } else { Write-Error "Not Running" }
}

function Start-DockerServices {
    Write-Info "Starting Docker services..."
    try {
        Push-Location "common"
        docker-compose up -d
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker services started successfully"
            Write-Info "Waiting 10 seconds for services to be ready..."
            Start-Sleep -Seconds 10
        } else {
            Write-Error "Failed to start Docker services"
        }
    } catch {
        Write-Error "Error starting Docker services: $_"
    } finally {
        Pop-Location
    }
}

function Stop-DockerServices {
    Write-Info "Stopping Docker services..."
    try {
        Push-Location "common"
        docker-compose down
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker services stopped successfully"
        } else {
            Write-Error "Failed to stop Docker services"
        }
    } catch {
        Write-Error "Error stopping Docker services: $_"
    } finally {
        Pop-Location
    }
}

function Build-Services {
    Write-Info "Building all services..."
    $mvnArgs = @("clean", "package")
    if (-not $VerboseOutput) { $mvnArgs += "-q" }
    
    try {
        & mvn @mvnArgs
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Build completed successfully"
        } else {
            Write-Error "Build failed"
        }
    } catch {
        Write-Error "Error during build: $_"
    }
}

function Start-ApplicationService {
    param([string]$ServiceName, [int]$Port, [string]$Module)
    
    Write-Info "Starting $ServiceName on port $Port..."
    
    $mvnArgs = @("-pl", $Module, "spring-boot:run")
    
    # Add profile arguments
    if ($Dev) {
        $mvnArgs += "-Pdev"
        $mvnArgs += "-Dspring-boot.run.jvmArguments=""-Dserver.port=$Port -Dlogging.level.com.simpleAnalytics=DEBUG"""
    } elseif ($Prod) {
        $mvnArgs += "-Pprod"  
        $mvnArgs += "-Dspring-boot.run.jvmArguments=""-Dserver.port=$Port"""
    } else {
        $mvnArgs += "-Dspring-boot.run.jvmArguments=""-Dserver.port=$Port"""
    }
    
    # Start in new window
    Start-Process -FilePath "cmd" -ArgumentList "/c", "title $ServiceName & mvn $($mvnArgs -join ' ') & pause"
    
    Write-Success "$ServiceName starting in new window..."
}

function Start-AllApplicationServices {
    Write-Info "Starting all application services..."
    Start-ApplicationService -ServiceName "TenetService" -Port 8080 -Module "TenetService"
    Start-Sleep -Seconds 5
    Start-ApplicationService -ServiceName "EventConsumer" -Port 8000 -Module "EventConsumer" 
    Start-Sleep -Seconds 5
    Start-ApplicationService -ServiceName "Gateway" -Port 8001 -Module "Gateway"
    
    Write-Success "All services are starting. Check the individual windows for logs."
    Write-Info "Services will be available at:"
    Write-Host "  - Gateway:       http://localhost:8001"
    Write-Host "  - EventConsumer: http://localhost:8000" 
    Write-Host "  - TenetService:  http://localhost:8080"
}

function Test-Services {
    Write-Info "Running tests..."
    $mvnArgs = @("test")
    if (-not $VerboseOutput) { $mvnArgs += "-q" }
    
    try {
        & mvn @mvnArgs
        if ($LASTEXITCODE -eq 0) {
            Write-Success "All tests passed"
        } else {
            Write-Error "Tests failed"
        }
    } catch {
        Write-Error "Error running tests: $_"
    }
}

function Clean-Services {
    Write-Info "Cleaning build artifacts..."
    try {
        & mvn clean
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Clean completed successfully"
        } else {
            Write-Error "Clean failed"
        }
    } catch {
        Write-Error "Error during clean: $_"
    }
}

# Main execution
switch ($Action) {
    "help" { Show-Help }
    
    "status" { Get-ServiceStatus }
    
    "build" { Build-Services }
    
    "test" { Test-Services }
    
    "clean" { Clean-Services }
    
    "start" {
        switch ($Service) {
            "docker" { Start-DockerServices }
            "gateway" { Start-ApplicationService -ServiceName "Gateway" -Port 8001 -Module "Gateway" }
            "eventconsumer" { Start-ApplicationService -ServiceName "EventConsumer" -Port 8000 -Module "EventConsumer" }
            "tenetservice" { Start-ApplicationService -ServiceName "TenetService" -Port 8080 -Module "TenetService" }
            "all" { 
                Start-DockerServices
                Start-AllApplicationServices
            }
        }
    }
    
    "stop" {
        switch ($Service) {
            "docker" { Stop-DockerServices }
            "all" { 
                Write-Info "To stop application services, close their respective windows or press Ctrl+C"
                Stop-DockerServices 
            }
            default { Write-Warning "Cannot stop individual application services. Close their windows or press Ctrl+C" }
        }
    }
    
    "restart" {
        Write-Info "Restarting services..."
        if ($Service -eq "docker" -or $Service -eq "all") {
            Stop-DockerServices
            Start-Sleep -Seconds 5
            Start-DockerServices
        }
        if ($Service -eq "all") {
            Start-AllApplicationServices
        }
    }
}