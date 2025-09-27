#!/bin/bash
# Event Ingestion Benchmark Runner for Linux/macOS
# Simple Analytics Event Ingestion Performance Testing

set -e

# Default values
GATEWAY_URL="http://localhost:8080"
EVENTS=1000
CONCURRENT=50
BATCH_SIZE=10
DURATION=0
APP_ID=$(python3 -c "import uuid; print(uuid.uuid4())" 2>/dev/null || echo "$(date +%s)-test-app")
AUTH_TOKEN=$(python3 -c "import uuid; print(uuid.uuid4())" 2>/dev/null || echo "$(date +%s)-auth-token")
OUTPUT_FILE=""
VERBOSE=false
START_SERVICES=false
STOP_SERVICES=false

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Print functions
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ️  $1${NC}"
}

# Help function
show_help() {
    cat << EOF
Event Ingestion Benchmark Runner

Usage: $0 [OPTIONS]

OPTIONS:
    --url URL               Gateway URL (default: http://localhost:8080)
    --events N              Total number of events (default: 1000)
    --concurrent N          Concurrent requests (default: 50)
    --batch-size N          Batch size for requests (default: 10)
    --duration N            Test duration in seconds (overrides --events)
    --app-id ID             Application ID (default: generated UUID)
    --auth-token TOKEN      Authorization token (default: generated UUID)
    --output FILE           Output file for results (JSON format)
    --verbose               Enable verbose logging
    --start-services        Start Docker services before benchmark
    --stop-services         Stop Docker services and exit
    --help                  Show this help message

EXAMPLES:
    # Basic benchmark
    $0

    # Start services and run benchmark
    $0 --start-services --events 1000

    # High-load test
    $0 --events 10000 --concurrent 100 --batch-size 20

    # Duration-based test
    $0 --duration 300 --concurrent 75

    # Save results to file
    $0 --events 1000 --output results.json --verbose

    # Stop services
    $0 --stop-services

EOF
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --url)
            GATEWAY_URL="$2"
            shift 2
        ;;
        --events)
            EVENTS="$2"
            shift 2
        ;;
        --concurrent)
            CONCURRENT="$2"
            shift 2
        ;;
        --batch-size)
            BATCH_SIZE="$2"
            shift 2
        ;;
        --duration)
            DURATION="$2"
            shift 2
        ;;
        --app-id)
            APP_ID="$2"
            shift 2
        ;;
        --auth-token)
            AUTH_TOKEN="$2"
            shift 2
        ;;
        --output)
            OUTPUT_FILE="$2"
            shift 2
        ;;
        --verbose)
            VERBOSE=true
            shift
        ;;
        --start-services)
            START_SERVICES=true
            shift
        ;;
        --stop-services)
            STOP_SERVICES=true
            shift
        ;;
        --help)
            show_help
            exit 0
        ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
        ;;
    esac
done

# Check if Python is available
check_python() {
    if command -v python3 &> /dev/null; then
        PYTHON_CMD="python3"
        print_success "Python3 found: $(python3 --version)"
        return 0
        elif command -v python &> /dev/null; then
        PYTHON_CMD="python"
        print_success "Python found: $(python --version)"
        return 0
    else
        print_error "Python is not available. Please install Python 3.7+ to run benchmarks."
        return 1
    fi
}

# Install Python dependencies
install_dependencies() {
    print_info "Installing Python dependencies..."
    if $PYTHON_CMD -m pip install aiohttp asyncio --quiet; then
        print_success "Dependencies installed successfully"
        return 0
    else
        print_error "Failed to install dependencies"
        return 1
    fi
}

# Start Docker services
start_services() {
    print_info "Starting Docker services..."
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    if (cd "$script_dir/../common" && docker-compose up -d); then
        print_success "Docker services started"
        print_info "Waiting 30 seconds for services to be ready..."
        sleep 30
        return 0
    else
        print_error "Failed to start Docker services"
        return 1
    fi
}

# Stop Docker services
stop_services() {
    print_info "Stopping Docker services..."
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    if (cd "$script_dir/../common" && docker-compose down); then
        print_success "Docker services stopped"
        return 0
    else
        print_error "Failed to stop Docker services"
        return 1
    fi
}

# Check if services are running
check_services() {
    print_info "Checking if services are running..."
    
    # Check Gateway
    if curl -s --max-time 5 "$GATEWAY_URL/actuator/health" > /dev/null 2>&1; then
        print_success "Gateway is running at $GATEWAY_URL"
    else
        print_warning "Gateway health check failed"
    fi
    
    # Check EventConsumer
    if curl -s --max-time 5 "http://localhost:8000/api/health/events" > /dev/null 2>&1; then
        print_success "EventConsumer is running"
    else
        print_warning "EventConsumer health check failed"
    fi
    
    # Check Kafka
    if nc -z localhost 9092 2>/dev/null; then
        print_success "Kafka is running on port 9092"
    else
        print_warning "Kafka is not accessible on port 9092"
    fi
}

# Run the benchmark
run_benchmark() {
    print_info "Starting event ingestion benchmark..."
    print_info "Configuration:"
    echo "  Gateway URL: $GATEWAY_URL"
    if [ "$DURATION" -gt 0 ]; then
        echo "  Test Duration: $DURATION seconds"
    else
        echo "  Total Events: $EVENTS"
    fi
    echo "  Concurrent Requests: $CONCURRENT"
    echo "  Batch Size: $BATCH_SIZE"
    echo "  App ID: $APP_ID"
    echo ""
    
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    local args=(
        "$script_dir/event-ingestion-benchmark.py"
        "--url" "$GATEWAY_URL"
        "--concurrent" "$CONCURRENT"
        "--batch-size" "$BATCH_SIZE"
        "--app-id" "$APP_ID"
        "--auth-token" "$AUTH_TOKEN"
    )
    
    if [ "$DURATION" -gt 0 ]; then
        args+=("--duration" "$DURATION")
    else
        args+=("--events" "$EVENTS")
    fi
    
    if [ -n "$OUTPUT_FILE" ]; then
        args+=("--output" "$OUTPUT_FILE")
    fi
    
    if [ "$VERBOSE" = true ]; then
        args+=("--verbose")
    fi
    
    if $PYTHON_CMD "${args[@]}"; then
        print_success "Benchmark completed successfully"
    else
        print_error "Benchmark failed with exit code $?"
        return 1
    fi
}

# Main execution
echo -e "${BLUE}============================================================${NC}"
echo -e "${BLUE}    SIMPLE ANALYTICS EVENT INGESTION BENCHMARK${NC}"
echo -e "${BLUE}============================================================${NC}"
echo ""

# Handle service management
if [ "$STOP_SERVICES" = true ]; then
    stop_services
    exit 0
fi

if [ "$START_SERVICES" = true ]; then
    if ! start_services; then
        print_error "Failed to start services. Exiting."
        exit 1
    fi
fi

# Check prerequisites
if ! check_python; then
    exit 1
fi

# Install dependencies
if ! install_dependencies; then
    exit 1
fi

# Check services
check_services

# Run benchmark
if ! run_benchmark; then
    exit 1
fi

echo ""
print_info "Benchmark completed. Check the results above."
if [ -n "$OUTPUT_FILE" ]; then
    print_info "Detailed results saved to: $OUTPUT_FILE"
fi