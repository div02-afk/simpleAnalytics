#!/usr/bin/env python3
"""
Event Ingestion Benchmark Script for Simple Analytics
Benchmarks the event ingestion pipeline performance by sending HTTP requests to the Gateway.
"""

import asyncio
import aiohttp
import time
import json
import uuid
import random
import statistics
from datetime import datetime, timezone
from dataclasses import dataclass, asdict
from typing import List, Dict, Any, Optional
import argparse
import sys
import logging
from concurrent.futures import ThreadPoolExecutor
import threading

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

@dataclass
class BenchmarkConfig:
    """Configuration class for benchmark parameters"""
    gateway_url: str = "http://localhost:8001/event"
    total_events: int = 1000
    concurrent_requests: int = 50
    batch_size: int = 10
    ramp_up_seconds: int = 10
    test_duration_seconds: Optional[int] = None
    app_id: str = str(uuid.uuid4())
    auth_token: str = str(uuid.uuid4())
    output_file: Optional[str] = None
    verbose: bool = False

@dataclass
class BenchmarkResult:
    """Results of benchmark execution"""
    total_events_sent: int
    total_events_failed: int
    total_time_seconds: float
    average_response_time_ms: float
    median_response_time_ms: float
    p95_response_time_ms: float
    p99_response_time_ms: float
    min_response_time_ms: float
    max_response_time_ms: float
    throughput_events_per_second: float
    error_rate_percentage: float
    errors: List[str]

class EventGenerator:
    """Generates realistic event data for benchmarking"""
    
    EVENT_TYPES = [
        "page_view", "button_click", "form_submit", "purchase", 
        "signup", "login", "logout", "search", "add_to_cart", "checkout"
    ]
    
    SOURCES = ["web", "mobile", "api", "analytics"]
    
    BROWSERS = ["Chrome", "Firefox", "Safari", "Edge", "Opera"]
    OS_LIST = ["Windows", "macOS", "Linux", "iOS", "Android"]
    DEVICES = ["Desktop", "Mobile", "Tablet"]
    LOCALES = ["en-US", "en-GB", "fr-FR", "de-DE", "es-ES", "ja-JP", "zh-CN"]
    TIMEZONES = ["UTC", "America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney"]
    
    def __init__(self, app_id: str):
        self.app_id = app_id
        self.user_pool = [str(uuid.uuid4()) for _ in range(100)]  # Pool of 100 users
        self.session_pool = [str(uuid.uuid4()) for _ in range(500)]  # Pool of 500 sessions
    
    def generate_event(self) -> Dict[str, Any]:
        """Generate a single realistic event"""
        now = datetime.now(timezone.utc)
        
        return {
            "appId": self.app_id,
            "anonymousId": str(uuid.uuid4()),
            "sessionId": random.choice(self.session_pool),
            "userId": random.choice(self.user_pool),
            "timestamp": now.isoformat(),
            "eventType": random.choice(self.EVENT_TYPES),
            "source": random.choice(self.SOURCES),
            "metadata": self._generate_metadata()
        }
    
    def _generate_metadata(self) -> Dict[str, Any]:
        """Generate realistic metadata for events"""
        metadata = {
            "page_url": f"https://example.com/{random.choice(['home', 'products', 'about', 'contact'])}",
            "referrer": random.choice([
                "https://google.com", "https://facebook.com", "direct", 
                "https://twitter.com", "email"
            ]),
            "user_agent": f"Mozilla/5.0 ({random.choice(self.OS_LIST)}) {random.choice(self.BROWSERS)}",
            "screen_resolution": random.choice(["1920x1080", "1366x768", "1440x900", "375x667"]),
            "viewport_size": random.choice(["1200x800", "375x667", "768x1024"]),
            "connection_type": random.choice(["wifi", "cellular", "ethernet"]),
            "device_type": random.choice(self.DEVICES),
            "browser": random.choice(self.BROWSERS),
            "os": random.choice(self.OS_LIST),
            "locale": random.choice(self.LOCALES),
            "timezone": random.choice(self.TIMEZONES)
        }
        
        # Add event-specific metadata
        event_metadata = random.choice([
            {"product_id": str(uuid.uuid4()), "price": round(random.uniform(10, 1000), 2)},
            {"search_query": random.choice(["analytics", "dashboard", "reports", "data"])},
            {"button_name": random.choice(["submit", "cancel", "save", "delete"])},
            {"form_name": random.choice(["contact", "signup", "login", "checkout"])},
            {}
        ])
        
        metadata.update(event_metadata)
        return metadata

class BenchmarkRunner:
    """Main benchmark execution class"""
    
    def __init__(self, config: BenchmarkConfig):
        self.config = config
        self.event_generator = EventGenerator(config.app_id)
        self.response_times: List[float] = []
        self.errors: List[str] = []
        self.events_sent = 0
        self.events_failed = 0
        self.lock = threading.Lock()
        
    async def send_event(self, session: aiohttp.ClientSession, event: Dict[str, Any]) -> bool:
        """Send a single event to the gateway"""
        headers = {
            # "Authorization": self.config.auth_token,
            "Content-Type": "application/json"
        }
        
        start_time = time.time()
        try:
            async with session.post(
                self.config.gateway_url,
                json=event,
                headers=headers,
                timeout=aiohttp.ClientTimeout(total=30)
            ) as response:
                end_time = time.time()
                response_time_ms = (end_time - start_time) * 1000
                
                with self.lock:
                    self.response_times.append(response_time_ms)
                    
                    if response.status == 200:
                        self.events_sent += 1
                        if self.config.verbose:
                            logger.info(f"✓ Event sent successfully in {response_time_ms:.2f}ms")
                        return True
                    else:
                        self.events_failed += 1
                        error_msg = f"HTTP {response.status}: {await response.text()}"
                        self.errors.append(error_msg)
                        logger.warning(f"✗ Event failed: {error_msg}")
                        return False
                        
        except Exception as e:
            end_time = time.time()
            response_time_ms = (end_time - start_time) * 1000
            
            with self.lock:
                self.response_times.append(response_time_ms)
                self.events_failed += 1
                error_msg = f"Exception: {str(e)}"
                self.errors.append(error_msg)
                logger.error(f"✗ Event failed with exception: {error_msg}")
                return False
    
    async def run_batch(self, session: aiohttp.ClientSession, batch_size: int) -> List[bool]:
        """Run a batch of concurrent requests"""
        events = [self.event_generator.generate_event() for _ in range(batch_size)]
        tasks = [self.send_event(session, event) for event in events]
        return await asyncio.gather(*tasks, return_exceptions=True)
    
    async def run_load_test(self) -> BenchmarkResult:
        """Run the main load test"""
        logger.info(f"Starting benchmark with {self.config.total_events} events")
        logger.info(f"Gateway URL: {self.config.gateway_url}")
        logger.info(f"Concurrent requests: {self.config.concurrent_requests}")
        logger.info(f"Batch size: {self.config.batch_size}")
        
        start_time = time.time()
        
        connector = aiohttp.TCPConnector(
            limit=self.config.concurrent_requests * 2,
            limit_per_host=self.config.concurrent_requests
        )
        
        timeout = aiohttp.ClientTimeout(total=30, connect=10)
        
        async with aiohttp.ClientSession(
            connector=connector, 
            timeout=timeout
        ) as session:
            
            # Warm up
            logger.info("Warming up...")
            await self.run_batch(session, min(10, self.config.batch_size))
            
            # Reset counters after warm up
            with self.lock:
                self.response_times.clear()
                self.errors.clear()
                self.events_sent = 0
                self.events_failed = 0
            
            # Main test execution
            logger.info("Starting main benchmark...")
            
            if self.config.test_duration_seconds:
                # Duration-based test
                await self._run_duration_based_test(session)
            else:
                # Event count-based test
                await self._run_count_based_test(session)
        
        end_time = time.time()
        total_time = end_time - start_time
        
        return self._calculate_results(total_time)
    
    async def _run_count_based_test(self, session: aiohttp.ClientSession):
        """Run test based on total number of events"""
        remaining_events = self.config.total_events
        batches = []
        
        while remaining_events > 0:
            current_batch_size = min(self.config.batch_size, remaining_events)
            batches.append(self.run_batch(session, current_batch_size))
            remaining_events -= current_batch_size
            
            # Control concurrency
            if len(batches) >= self.config.concurrent_requests:
                await asyncio.gather(*batches)
                batches = []
                
                # Progress update
                completed = self.config.total_events - remaining_events
                progress = (completed / self.config.total_events) * 100
                logger.info(f"Progress: {completed}/{self.config.total_events} ({progress:.1f}%)")
        
        # Wait for remaining batches
        if batches:
            await asyncio.gather(*batches)
    
    async def _run_duration_based_test(self, session: aiohttp.ClientSession):
        """Run test for a specified duration"""
        end_time = time.time() + self.config.test_duration_seconds
        batches = []
        
        while time.time() < end_time:
            batches.append(self.run_batch(session, self.config.batch_size))
            
            # Control concurrency
            if len(batches) >= self.config.concurrent_requests:
                await asyncio.gather(*batches)
                batches = []
                
                # Progress update
                elapsed = time.time() - (end_time - self.config.test_duration_seconds)
                progress = (elapsed / self.config.test_duration_seconds) * 100
                logger.info(f"Progress: {elapsed:.1f}s/{self.config.test_duration_seconds}s ({progress:.1f}%)")
        
        # Wait for remaining batches
        if batches:
            await asyncio.gather(*batches)
    
    def _calculate_results(self, total_time: float) -> BenchmarkResult:
        """Calculate and return benchmark results"""
        total_events = self.events_sent + self.events_failed
        
        if not self.response_times:
            logger.error("No response times recorded!")
            return BenchmarkResult(
                total_events_sent=0,
                total_events_failed=0,
                total_time_seconds=total_time,
                average_response_time_ms=0,
                median_response_time_ms=0,
                p95_response_time_ms=0,
                p99_response_time_ms=0,
                min_response_time_ms=0,
                max_response_time_ms=0,
                throughput_events_per_second=0,
                error_rate_percentage=100,
                errors=self.errors
            )
        
        # Calculate statistics
        avg_response_time = statistics.mean(self.response_times)
        median_response_time = statistics.median(self.response_times)
        
        sorted_times = sorted(self.response_times)
        p95_index = int(len(sorted_times) * 0.95)
        p99_index = int(len(sorted_times) * 0.99)
        
        p95_response_time = sorted_times[p95_index] if p95_index < len(sorted_times) else sorted_times[-1]
        p99_response_time = sorted_times[p99_index] if p99_index < len(sorted_times) else sorted_times[-1]
        
        min_response_time = min(self.response_times)
        max_response_time = max(self.response_times)
        
        throughput = total_events / total_time if total_time > 0 else 0
        error_rate = (self.events_failed / total_events * 100) if total_events > 0 else 0
        
        return BenchmarkResult(
            total_events_sent=self.events_sent,
            total_events_failed=self.events_failed,
            total_time_seconds=total_time,
            average_response_time_ms=avg_response_time,
            median_response_time_ms=median_response_time,
            p95_response_time_ms=p95_response_time,
            p99_response_time_ms=p99_response_time,
            min_response_time_ms=min_response_time,
            max_response_time_ms=max_response_time,
            throughput_events_per_second=throughput,
            error_rate_percentage=error_rate,
            errors=self.errors[:50]  # Limit to first 50 errors
        )

def print_results(result: BenchmarkResult):
    """Print benchmark results in a formatted way"""
    print("\n" + "="*60)
    print("           EVENT INGESTION BENCHMARK RESULTS")
    print("="*60)
    
    print(f"\n📊 SUMMARY:")
    print(f"  Total Events Sent:     {result.total_events_sent:,}")
    print(f"  Total Events Failed:   {result.total_events_failed:,}")
    print(f"  Total Test Time:       {result.total_time_seconds:.2f} seconds")
    print(f"  Throughput:           {result.throughput_events_per_second:.2f} events/second")
    print(f"  Error Rate:           {result.error_rate_percentage:.2f}%")
    
    print(f"\n⚡ RESPONSE TIMES:")
    print(f"  Average:              {result.average_response_time_ms:.2f} ms")
    print(f"  Median:               {result.median_response_time_ms:.2f} ms")
    print(f"  95th Percentile:      {result.p95_response_time_ms:.2f} ms")
    print(f"  99th Percentile:      {result.p99_response_time_ms:.2f} ms")
    print(f"  Min:                  {result.min_response_time_ms:.2f} ms")
    print(f"  Max:                  {result.max_response_time_ms:.2f} ms")
    
    if result.errors:
        print(f"\n❌ ERRORS (showing first 10):")
        for i, error in enumerate(result.errors[:10], 1):
            print(f"  {i}. {error}")
        if len(result.errors) > 10:
            print(f"  ... and {len(result.errors) - 10} more errors")
    
    # Performance assessment
    print(f"\n🎯 PERFORMANCE ASSESSMENT:")
    if result.error_rate_percentage < 1:
        print("  ✅ Error rate is excellent (< 1%)")
    elif result.error_rate_percentage < 5:
        print("  ⚠️  Error rate is acceptable (< 5%)")
    else:
        print("  ❌ Error rate is high (>= 5%)")
    
    if result.average_response_time_ms < 100:
        print("  ✅ Average response time is excellent (< 100ms)")
    elif result.average_response_time_ms < 500:
        print("  ⚠️  Average response time is acceptable (< 500ms)")
    else:
        print("  ❌ Average response time is slow (>= 500ms)")
    
    if result.throughput_events_per_second > 100:
        print("  ✅ Throughput is excellent (> 100 events/s)")
    elif result.throughput_events_per_second > 50:
        print("  ⚠️  Throughput is acceptable (> 50 events/s)")
    else:
        print("  ❌ Throughput is low (<= 50 events/s)")
    
    print("="*60)

def save_results_to_file(result: BenchmarkResult, filename: str):
    """Save results to a JSON file"""
    output_data = {
        "timestamp": datetime.now().isoformat(),
        "results": asdict(result)
    }
    
    with open(filename, 'w') as f:
        json.dump(output_data, f, indent=2)
    
    logger.info(f"Results saved to {filename}")

async def main():
    """Main function"""
    parser = argparse.ArgumentParser(description="Event Ingestion Benchmark Tool")
    
    parser.add_argument("--url", default="http://localhost:8001/event", 
                       help="Gateway URL (default: http://localhost:8001/event)")
    parser.add_argument("--events", type=int, default=10000,
                       help="Total number of events to send (default: 1000)")
    parser.add_argument("--concurrent", type=int, default=100,
                       help="Number of concurrent requests (default: 50)")
    parser.add_argument("--batch-size", type=int, default=10,
                       help="Batch size for requests (default: 10)")
    parser.add_argument("--duration", type=int, 
                       help="Test duration in seconds (overrides --events)")
    parser.add_argument("--app-id", default=str(uuid.uuid4()),
                       help="Application ID to use for events")
    parser.add_argument("--auth-token", default=str(uuid.uuid4()),
                       help="Authorization token")
    parser.add_argument("--output", help="Output file for results (JSON format)")
    parser.add_argument("--verbose", action="store_true",
                       help="Enable verbose logging")
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    config = BenchmarkConfig(
        gateway_url=args.url,
        total_events=args.events,
        concurrent_requests=args.concurrent,
        batch_size=args.batch_size,
        test_duration_seconds=args.duration,
        app_id=args.app_id,
        auth_token=args.auth_token,
        output_file=args.output,
        verbose=args.verbose
    )
    
    runner = BenchmarkRunner(config)
    
    try:
        result = await runner.run_load_test()
        print_results(result)
        
        if args.output:
            save_results_to_file(result, args.output)
            
    except KeyboardInterrupt:
        logger.info("Benchmark interrupted by user")
        sys.exit(1)
    except Exception as e:
        logger.error(f"Benchmark failed: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    asyncio.run(main())