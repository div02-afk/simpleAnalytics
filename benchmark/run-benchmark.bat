@echo off
REM Event Ingestion Benchmark Runner for Windows CMD
REM Simple Analytics Event Ingestion Performance Testing

setlocal enabledelayedexpansion

REM Default values
set "GATEWAY_URL=http://localhost:8080"
set "EVENTS=1000"
set "CONCURRENT=50"
set "BATCH_SIZE=10"
set "DURATION=0"
set "APP_ID=%RANDOM%-%RANDOM%-test-app"
set "AUTH_TOKEN=%RANDOM%-%RANDOM%-auth-token"
set "OUTPUT_FILE="
set "VERBOSE=false"
set "START_SERVICES=false"
set "STOP_SERVICES=false"

REM Parse command line arguments
:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="--url" (
    set "GATEWAY_URL=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--events" (
    set "EVENTS=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--concurrent" (
    set "CONCURRENT=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--batch-size" (
    set "BATCH_SIZE=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--duration" (
    set "DURATION=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--app-id" (
    set "APP_ID=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--auth-token" (
    set "AUTH_TOKEN=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--output" (
    set "OUTPUT_FILE=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--verbose" (
    set "VERBOSE=true"
    shift
    goto parse_args
)
if "%~1"=="--start-services" (
    set "START_SERVICES=true"
    shift
    goto parse_args
)
if "%~1"=="--stop-services" (
    set "STOP_SERVICES=true"
    shift
    goto parse_args
)
if "%~1"=="--help" (
    goto show_help
)
echo Unknown option: %~1
goto show_help

:end_parse

REM Print functions
goto main

:print_success
echo [92m✓ %~1[0m
goto :eof

:print_warning
echo [93m⚠ %~1[0m
goto :eof

:print_error
echo [91m✗ %~1[0m
goto :eof

:print_info
echo [96mℹ %~1[0m
goto :eof

:show_help
echo Event Ingestion Benchmark Runner
echo.
echo Usage: %~n0 [OPTIONS]
echo.
echo OPTIONS:
echo     --url URL               Gateway URL (default: http://localhost:8080)
echo     --events N              Total number of events (default: 1000)
echo     --concurrent N          Concurrent requests (default: 50)
echo     --batch-size N          Batch size for requests (default: 10)
echo     --duration N            Test duration in seconds (overrides --events)
echo     --app-id ID             Application ID (default: generated)
echo     --auth-token TOKEN      Authorization token (default: generated)
echo     --output FILE           Output file for results (JSON format)
echo     --verbose               Enable verbose logging
echo     --start-services        Start Docker services before benchmark
echo     --stop-services         Stop Docker services and exit
echo     --help                  Show this help message
echo.
echo EXAMPLES:
echo     %~n0
echo     %~n0 --start-services --events 1000
echo     %~n0 --events 10000 --concurrent 100 --batch-size 20
echo     %~n0 --duration 300 --concurrent 75
echo     %~n0 --events 1000 --output results.json --verbose
echo     %~n0 --stop-services
goto :eof

:check_python
python --version >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Python found"
    set "PYTHON_CMD=python"
    goto :eof
)
py --version >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Python (py) found"
    set "PYTHON_CMD=py"
    goto :eof
)
call :print_error "Python is not available. Please install Python 3.7+"
exit /b 1

:install_dependencies
call :print_info "Installing Python dependencies..."
%PYTHON_CMD% -m pip install aiohttp asyncio --quiet
if %errorlevel% equ 0 (
    call :print_success "Dependencies installed successfully"
    goto :eof
) else (
    call :print_error "Failed to install dependencies"
    exit /b 1
)

:start_services
call :print_info "Starting Docker services..."
pushd "%~dp0..\common"
docker-compose up -d
if %errorlevel% equ 0 (
    call :print_success "Docker services started"
    call :print_info "Waiting 30 seconds for services to be ready..."
    timeout /t 30 /nobreak >nul
    popd
    goto :eof
) else (
    call :print_error "Failed to start Docker services"
    popd
    exit /b 1
)

:stop_services
call :print_info "Stopping Docker services..."
pushd "%~dp0..\common"
docker-compose down
if %errorlevel% equ 0 (
    call :print_success "Docker services stopped"
    popd
    goto :eof
) else (
    call :print_error "Failed to stop Docker services"
    popd
    exit /b 1
)

:check_services
call :print_info "Checking if services are running..."

REM Check Gateway (simplified)
curl -s --max-time 5 "%GATEWAY_URL%/actuator/health" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Gateway appears to be running at %GATEWAY_URL%"
) else (
    call :print_warning "Gateway health check failed"
)

REM Check EventConsumer
curl -s --max-time 5 "http://localhost:8000/api/health/events" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "EventConsumer appears to be running"
) else (
    call :print_warning "EventConsumer health check failed"
)

goto :eof

:run_benchmark
call :print_info "Starting event ingestion benchmark..."
call :print_info "Configuration:"
echo   Gateway URL: %GATEWAY_URL%
if %DURATION% gtr 0 (
    echo   Test Duration: %DURATION% seconds
) else (
    echo   Total Events: %EVENTS%
)
echo   Concurrent Requests: %CONCURRENT%
echo   Batch Size: %BATCH_SIZE%
echo   App ID: %APP_ID%
echo.

set "ARGS=%~dp0event-ingestion-benchmark.py --url %GATEWAY_URL% --concurrent %CONCURRENT% --batch-size %BATCH_SIZE% --app-id %APP_ID% --auth-token %AUTH_TOKEN%"

if %DURATION% gtr 0 (
    set "ARGS=%ARGS% --duration %DURATION%"
) else (
    set "ARGS=%ARGS% --events %EVENTS%"
)

if not "%OUTPUT_FILE%"=="" (
    set "ARGS=%ARGS% --output %OUTPUT_FILE%"
)

if "%VERBOSE%"=="true" (
    set "ARGS=%ARGS% --verbose"
)

%PYTHON_CMD% %ARGS%
if %errorlevel% equ 0 (
    call :print_success "Benchmark completed successfully"
    goto :eof
) else (
    call :print_error "Benchmark failed with exit code %errorlevel%"
    exit /b 1
)

:main
echo ============================================================
echo     SIMPLE ANALYTICS EVENT INGESTION BENCHMARK
echo ============================================================
echo.

REM Handle service management
if "%STOP_SERVICES%"=="true" (
    call :stop_services
    exit /b 0
)

if "%START_SERVICES%"=="true" (
    call :start_services
    if !errorlevel! neq 0 (
        call :print_error "Failed to start services. Exiting."
        exit /b 1
    )
)

REM Check prerequisites
call :check_python
if %errorlevel% neq 0 exit /b 1

REM Install dependencies
call :install_dependencies
if %errorlevel% neq 0 exit /b 1

REM Check services
call :check_services

REM Run benchmark
call :run_benchmark
if %errorlevel% neq 0 exit /b 1

echo.
call :print_info "Benchmark completed. Check the results above."
if not "%OUTPUT_FILE%"=="" (
    call :print_info "Detailed results saved to: %OUTPUT_FILE%"
)