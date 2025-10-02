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

# Existing Application IDs from data-seed.sql
EXISTING_APP_IDS = [
    # Acme Corporation Applications
    "770e8400-e29b-41d4-a716-446655440001",  # Acme Web Analytics
    "770e8400-e29b-41d4-a716-446655440002",  # Acme Mobile App
    "770e8400-e29b-41d4-a716-446655440003",  # Acme API Gateway
    # TechStart Inc Applications  
    "770e8400-e29b-41d4-a716-446655440004",  # TechStart Dashboard
    "770e8400-e29b-41d4-a716-446655440005",  # TechStart Mobile
    # Global Analytics Ltd Applications
    "770e8400-e29b-41d4-a716-446655440006",  # Global Web Platform
    "770e8400-e29b-41d4-a716-446655440007",  # Global Mobile Analytics
    "770e8400-e29b-41d4-a716-446655440008",  # Global Data Pipeline
    "770e8400-e29b-41d4-a716-446655440009",  # Global IoT Analytics
    # Startup Ventures Applications
    "770e8400-e29b-41d4-a716-446655440010",  # Startup MVP Analytics
    # Enterprise Solutions Applications
    "770e8400-e29b-41d4-a716-446655440011",  # Enterprise Portal
    "770e8400-e29b-41d4-a716-446655440012",  # Enterprise Mobile Suite
    "770e8400-e29b-41d4-a716-446655440013",  # Enterprise API Analytics
    # Digital Innovations Applications
    "770e8400-e29b-41d4-a716-446655440014",  # Digital Web Tracker
    "770e8400-e29b-41d4-a716-446655440015",  # Digital App Analytics
    # Data Insights Co Applications
    "770e8400-e29b-41d4-a716-446655440016",  # Data Insights Dashboard
    "770e8400-e29b-41d4-a716-446655440017",  # Data Insights API
]

@dataclass
class BenchmarkConfig:
    """Configuration class for benchmark parameters"""
    gateway_url: str = "http://localhost:8001/event"
    total_events: int = 1000
    concurrent_requests: int = 50
    batch_size: int = 10
    ramp_up_seconds: int = 10
    test_duration_seconds: Optional[int] = None
    app_id: str = random.choice(EXISTING_APP_IDS)  # Default to random existing app
    auth_token: str = str(uuid.uuid4())
    output_file: Optional[str] = None
    verbose: bool = False
    use_existing_apps: bool = True  # New flag to control app ID behavior
    # Rate limiting options
    target_rps: Optional[int] = None  # Target requests per second
    ramp_up_to_target: bool = False  # Whether to ramp up to target RPS
    ramp_duration_seconds: int = 60  # Time to ramp up to target RPS
    rate_limit_mode: bool = False  # Use rate limiting instead of concurrent batching

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
    # Enhanced metrics
    p75_response_time_ms: float = 0.0
    p90_response_time_ms: float = 0.0
    std_dev_response_time_ms: float = 0.0
    target_rps: Optional[int] = None
    actual_rps_achieved: float = 0.0
    rps_accuracy_percentage: float = 0.0
    time_series_data: List[Dict[str, Any]] = None  # For tracking metrics over time

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
    
    def __init__(self, app_id: str, use_existing_apps: bool = True):
        self.app_id = app_id
        self.use_existing_apps = use_existing_apps
        self.user_pool = [str(uuid.uuid4()) for _ in range(100)]  # Pool of 100 users
        self.session_pool = [str(uuid.uuid4()) for _ in range(500)]  # Pool of 500 sessions
    
    def generate_event(self) -> Dict[str, Any]:
        """Generate a single realistic event"""
        now = datetime.now(timezone.utc)
        
        # Use random existing app ID if enabled, otherwise use the configured one
        app_id = random.choice(EXISTING_APP_IDS) if self.use_existing_apps else self.app_id
        
        return {
            "appId": app_id,
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
        self.event_generator = EventGenerator(config.app_id, config.use_existing_apps)
        self.response_times: List[float] = []
        self.errors: List[str] = []
        self.events_sent = 0
        self.events_failed = 0
        self.lock = threading.Lock()
        self.time_series_data: List[Dict[str, Any]] = []
        self.start_time = None
        
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
        if self.config.rate_limit_mode and self.config.target_rps:
            return await self.run_rate_limited_test()
        else:
            return await self.run_traditional_load_test()
    
    async def run_rate_limited_test(self) -> BenchmarkResult:
        """Run rate-limited test with gradual ramp-up"""
        logger.info(f"Starting rate-limited benchmark")
        logger.info(f"Target RPS: {self.config.target_rps}")
        logger.info(f"Ramp duration: {self.config.ramp_duration_seconds}s")
        logger.info(f"Test duration: {self.config.test_duration_seconds or 'unlimited'}s")
        
        self.start_time = time.time()
        
        connector = aiohttp.TCPConnector(
            limit=self.config.target_rps * 2,
            limit_per_host=self.config.target_rps * 2
        )
        
        timeout = aiohttp.ClientTimeout(total=30, connect=10)
        
        async with aiohttp.ClientSession(
            connector=connector, 
            timeout=timeout
        ) as session:
            # Warm up
            logger.info("Warming up...")
            await self.run_batch(session, 5)
            
            # Reset counters after warm up
            with self.lock:
                self.response_times.clear()
                self.errors.clear()
                self.events_sent = 0
                self.events_failed = 0
                self.time_series_data.clear()
            
            self.start_time = time.time()  # Reset start time after warmup
            
            # Main rate-limited test
            await self._run_rate_limited_requests(session)
        
        end_time = time.time()
        total_time = end_time - self.start_time
        
        return self._calculate_enhanced_results(total_time)
    
    async def _run_rate_limited_requests(self, session: aiohttp.ClientSession):
        """Execute rate-limited requests with ramping"""
        test_duration = self.config.test_duration_seconds or 300  # Default 5 minutes
        ramp_duration = min(self.config.ramp_duration_seconds, test_duration)
        target_rps = self.config.target_rps
        
        tasks = []
        interval_duration = 1.0  # 1-second intervals for rate tracking
        next_interval = time.time() + interval_duration
        
        for elapsed in range(int(test_duration)):
            current_time = time.time()
            
            # Calculate current target RPS (ramp up)
            if self.config.ramp_up_to_target and elapsed < ramp_duration:
                current_rps = int((elapsed / ramp_duration) * target_rps)
                current_rps = max(1, current_rps)  # At least 1 RPS
            else:
                current_rps = target_rps
            
            # Send requests for this second
            requests_this_second = []
            request_interval = 1.0 / current_rps if current_rps > 0 else 1.0
            
            for i in range(current_rps):
                event = self.event_generator.generate_event()
                task = asyncio.create_task(self.send_event(session, event))
                requests_this_second.append(task)
                
                # Add small delay between requests within the second
                if i < current_rps - 1:  # Don't delay after the last request
                    await asyncio.sleep(request_interval)
            
            tasks.extend(requests_this_second)
            
            # Wait for completion of requests and record metrics every interval
            if current_time >= next_interval:
                # Wait for recent requests to complete
                if requests_this_second:
                    await asyncio.gather(*requests_this_second, return_exceptions=True)
                
                # Record metrics for this interval
                self._record_interval_metrics(elapsed, current_rps)
                
                next_interval = current_time + interval_duration
                
                # Progress update
                if elapsed % 10 == 0:  # Every 10 seconds
                    logger.info(f"Time: {elapsed}s, Target RPS: {current_rps}, Sent: {self.events_sent}, Failed: {self.events_failed}")
            
            # Sleep until the next second
            await asyncio.sleep(max(0, 1.0 - (time.time() - current_time)))
        
        # Wait for any remaining tasks
        if tasks:
            await asyncio.gather(*tasks, return_exceptions=True)
    
    def _record_interval_metrics(self, elapsed_seconds: int, target_rps: int):
        """Record metrics for the current time interval"""
        current_time = time.time()
        interval_start = current_time - 1.0  # Last 1 second
        
        # Calculate metrics for this interval
        recent_response_times = []
        recent_errors = 0
        
        with self.lock:
            # This is simplified - in a real implementation, you'd track timestamps
            # For now, we'll estimate based on recent activity
            if self.response_times:
                recent_count = min(target_rps, len(self.response_times))
                recent_response_times = self.response_times[-recent_count:]
            
            recent_errors = max(0, len(self.errors) - getattr(self, '_last_error_count', 0))
            self._last_error_count = len(self.errors)
        
        # Calculate interval metrics
        interval_data = {
            'timestamp': current_time,
            'elapsed_seconds': elapsed_seconds,
            'target_rps': target_rps,
            'actual_requests': len(recent_response_times),
            'errors': recent_errors,
            'avg_latency_ms': statistics.mean(recent_response_times) if recent_response_times else 0,
            'p95_latency_ms': statistics.quantiles(recent_response_times, n=20)[18] if len(recent_response_times) >= 20 else (max(recent_response_times) if recent_response_times else 0),
            'total_sent': self.events_sent,
            'total_failed': self.events_failed
        }
        
        self.time_series_data.append(interval_data)
        
    async def run_traditional_load_test(self) -> BenchmarkResult:
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
    
    def _calculate_enhanced_results(self, total_time: float) -> BenchmarkResult:
        """Calculate enhanced benchmark results with detailed metrics"""
        total_events = self.events_sent + self.events_failed
        
        if not self.response_times:
            logger.error("No response times recorded!")
            return self._create_empty_result(total_time)
        
        # Calculate percentiles and statistics
        sorted_times = sorted(self.response_times)
        avg_response_time = statistics.mean(sorted_times)
        median_response_time = statistics.median(sorted_times)
        std_dev_response_time = statistics.stdev(sorted_times) if len(sorted_times) > 1 else 0
        
        # Calculate percentiles
        percentiles = statistics.quantiles(sorted_times, n=100)
        p75_response_time = percentiles[74] if len(percentiles) >= 75 else sorted_times[-1]
        p90_response_time = percentiles[89] if len(percentiles) >= 90 else sorted_times[-1]
        p95_response_time = percentiles[94] if len(percentiles) >= 95 else sorted_times[-1]
        p99_response_time = percentiles[98] if len(percentiles) >= 99 else sorted_times[-1]
        
        min_response_time = min(sorted_times)
        max_response_time = max(sorted_times)
        
        throughput = total_events / total_time if total_time > 0 else 0
        error_rate = (self.events_failed / total_events * 100) if total_events > 0 else 0
        
        # Rate limiting specific metrics
        actual_rps = self.events_sent / total_time if total_time > 0 else 0
        target_rps = self.config.target_rps
        rps_accuracy = ((actual_rps / target_rps) * 100) if target_rps else 100
        
        return BenchmarkResult(
            total_events_sent=self.events_sent,
            total_events_failed=self.events_failed,
            total_time_seconds=total_time,
            average_response_time_ms=avg_response_time,
            median_response_time_ms=median_response_time,
            p75_response_time_ms=p75_response_time,
            p90_response_time_ms=p90_response_time,
            p95_response_time_ms=p95_response_time,
            p99_response_time_ms=p99_response_time,
            min_response_time_ms=min_response_time,
            max_response_time_ms=max_response_time,
            std_dev_response_time_ms=std_dev_response_time,
            throughput_events_per_second=throughput,
            error_rate_percentage=error_rate,
            errors=self.errors[:50],  # Limit to first 50 errors
            target_rps=target_rps,
            actual_rps_achieved=actual_rps,
            rps_accuracy_percentage=rps_accuracy,
            time_series_data=self.time_series_data.copy()
        )
    
    def _create_empty_result(self, total_time: float) -> BenchmarkResult:
        """Create empty result for failed tests"""
        return BenchmarkResult(
            total_events_sent=0,
            total_events_failed=0,
            total_time_seconds=total_time,
            average_response_time_ms=0,
            median_response_time_ms=0,
            p75_response_time_ms=0,
            p90_response_time_ms=0,
            p95_response_time_ms=0,
            p99_response_time_ms=0,
            min_response_time_ms=0,
            max_response_time_ms=0,
            std_dev_response_time_ms=0,
            throughput_events_per_second=0,
            error_rate_percentage=100,
            errors=self.errors,
            target_rps=self.config.target_rps,
            actual_rps_achieved=0,
            rps_accuracy_percentage=0,
            time_series_data=[]
        )
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
    print("\n" + "="*70)
    print("             EVENT INGESTION BENCHMARK RESULTS")
    print("="*70)
    
    print(f"\n📊 SUMMARY:")
    print(f"  Total Events Sent:     {result.total_events_sent:,}")
    print(f"  Total Events Failed:   {result.total_events_failed:,}")
    print(f"  Total Test Time:       {result.total_time_seconds:.2f} seconds")
    print(f"  Throughput:           {result.throughput_events_per_second:.2f} events/second")
    print(f"  Error Rate:           {result.error_rate_percentage:.2f}%")
    
    # Rate limiting specific metrics
    if hasattr(result, 'target_rps') and result.target_rps:
        print(f"\n🎯 RATE LIMITING:")
        print(f"  Target RPS:           {result.target_rps}")
        print(f"  Actual RPS:           {result.actual_rps_achieved:.2f}")
        print(f"  RPS Accuracy:         {result.rps_accuracy_percentage:.1f}%")
    
    print(f"\n⚡ RESPONSE TIMES:")
    print(f"  Average:              {result.average_response_time_ms:.2f} ms")
    print(f"  Median:               {result.median_response_time_ms:.2f} ms")
    
    # Show enhanced percentiles if available
    if hasattr(result, 'p75_response_time_ms') and result.p75_response_time_ms:
        print(f"  75th Percentile:      {result.p75_response_time_ms:.2f} ms")
        print(f"  90th Percentile:      {result.p90_response_time_ms:.2f} ms")
    
    print(f"  95th Percentile:      {result.p95_response_time_ms:.2f} ms")
    print(f"  99th Percentile:      {result.p99_response_time_ms:.2f} ms")
    print(f"  Min:                  {result.min_response_time_ms:.2f} ms")
    print(f"  Max:                  {result.max_response_time_ms:.2f} ms")
    
    # Show standard deviation if available
    if hasattr(result, 'std_dev_response_time_ms') and result.std_dev_response_time_ms:
        print(f"  Std Deviation:        {result.std_dev_response_time_ms:.2f} ms")
    
    # Show time series summary if available
    if hasattr(result, 'time_series_data') and result.time_series_data:
        print(f"\n📈 TIME SERIES DATA:")
        print(f"  Data Points Collected: {len(result.time_series_data)}")
        if len(result.time_series_data) > 0:
            avg_actual_rps = statistics.mean([d['actual_requests'] for d in result.time_series_data])
            avg_latency = statistics.mean([d['avg_latency_ms'] for d in result.time_series_data if d['avg_latency_ms'] > 0])
            print(f"  Avg Interval RPS:     {avg_actual_rps:.1f}")
            print(f"  Avg Interval Latency: {avg_latency:.2f} ms")
    
    if result.errors:
        print(f"\n❌ ERRORS (showing first 10):")
        for i, error in enumerate(result.errors[:10], 1):
            print(f"  {i}. {error}")
        if len(result.errors) > 10:
            print(f"  ... and {len(result.errors) - 10} more errors")
    
    # Enhanced performance assessment
    print(f"\n🎯 PERFORMANCE ASSESSMENT:")
    
    # Error rate assessment
    if result.error_rate_percentage < 0.1:
        print("  ✅ Error rate is exceptional (< 0.1%)")
    elif result.error_rate_percentage < 1:
        print("  ✅ Error rate is excellent (< 1%)")
    elif result.error_rate_percentage < 5:
        print("  ⚠️  Error rate is acceptable (< 5%)")
    else:
        print("  ❌ Error rate is high (>= 5%)")
    
    # Latency assessment
    if result.average_response_time_ms < 50:
        print("  ✅ Average latency is exceptional (< 50ms)")
    elif result.average_response_time_ms < 100:
        print("  ✅ Average latency is excellent (< 100ms)")
    elif result.average_response_time_ms < 250:
        print("  ⚠️  Average latency is acceptable (< 250ms)")
    elif result.average_response_time_ms < 500:
        print("  ⚠️  Average latency is slow (< 500ms)")
    else:
        print("  ❌ Average latency is very slow (>= 500ms)")
    
    # P95 latency assessment
    if result.p95_response_time_ms < 100:
        print("  ✅ P95 latency is excellent (< 100ms)")
    elif result.p95_response_time_ms < 250:
        print("  ⚠️  P95 latency is acceptable (< 250ms)")
    elif result.p95_response_time_ms < 1000:
        print("  ⚠️  P95 latency is slow (< 1000ms)")
    else:
        print("  ❌ P95 latency is very slow (>= 1000ms)")
    
    # Throughput assessment
    if result.throughput_events_per_second > 1000:
        print("  ✅ Throughput is exceptional (> 1000 events/s)")
    elif result.throughput_events_per_second > 500:
        print("  ✅ Throughput is excellent (> 500 events/s)")
    elif result.throughput_events_per_second > 100:
        print("  ⚠️  Throughput is good (> 100 events/s)")
    elif result.throughput_events_per_second > 50:
        print("  ⚠️  Throughput is acceptable (> 50 events/s)")
    else:
        print("  ❌ Throughput is low (<= 50 events/s)")
    
    # Rate limiting accuracy assessment
    if hasattr(result, 'target_rps') and result.target_rps and hasattr(result, 'rps_accuracy_percentage'):
        if result.rps_accuracy_percentage > 95:
            print("  ✅ Rate limiting accuracy is excellent (> 95%)")
        elif result.rps_accuracy_percentage > 85:
            print("  ⚠️  Rate limiting accuracy is acceptable (> 85%)")
        else:
            print("  ❌ Rate limiting accuracy is poor (<= 85%)")
    
    print("="*70)

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
    parser.add_argument("--app-id", default=random.choice(EXISTING_APP_IDS),
                       help="Application ID to use for events (default: random existing app)")
    parser.add_argument("--auth-token", default=str(uuid.uuid4()),
                       help="Authorization token")
    parser.add_argument("--output", help="Output file for results (JSON format)")
    parser.add_argument("--verbose", action="store_true",
                       help="Enable verbose logging")
    parser.add_argument("--use-existing-apps", action="store_true", default=True,
                       help="Use random existing app IDs from database (default: True)")
    parser.add_argument("--single-app", action="store_true",
                       help="Use only the specified app-id instead of random existing apps")
    parser.add_argument("--list-apps", action="store_true",
                       help="List all available existing app IDs and exit")
    
    # Rate limiting arguments
    parser.add_argument("--target-rps", type=int,
                       help="Target requests per second (enables rate limiting mode)")
    parser.add_argument("--ramp-up", action="store_true",
                       help="Gradually ramp up to target RPS (requires --target-rps)")
    parser.add_argument("--ramp-duration", type=int, default=60,
                       help="Duration in seconds to ramp up to target RPS (default: 60)")
    parser.add_argument("--rate-duration", type=int, default=300,
                       help="Total duration for rate-limited test in seconds (default: 300)")
    
    args = parser.parse_args()
    
    # Handle list apps command
    if args.list_apps:
        print("\n📱 Available Application IDs from database:")
        print("="*60)
        app_names = [
            "Acme Web Analytics", "Acme Mobile App", "Acme API Gateway",
            "TechStart Dashboard", "TechStart Mobile", "Global Web Platform",
            "Global Mobile Analytics", "Global Data Pipeline", "Global IoT Analytics",
            "Startup MVP Analytics", "Enterprise Portal", "Enterprise Mobile Suite",
            "Enterprise API Analytics", "Digital Web Tracker", "Digital App Analytics",
            "Data Insights Dashboard", "Data Insights API"
        ]
        for i, (app_id, name) in enumerate(zip(EXISTING_APP_IDS, app_names), 1):
            print(f"  {i:2d}. {app_id} - {name}")
        print("\nUse --app-id <ID> to test with a specific app")
        print("Use --single-app to use only that app instead of random selection")
        return
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # Determine app behavior
    use_existing_apps = args.use_existing_apps and not args.single_app
    
    # Validate rate limiting arguments
    if args.target_rps:
        if args.ramp_up and not args.ramp_duration:
            logger.error("--ramp-duration must be specified when using --ramp-up")
            sys.exit(1)
        
        # Override duration for rate-limited tests
        test_duration = args.rate_duration
        if args.duration:
            test_duration = args.duration
            
        config = BenchmarkConfig(
            gateway_url=args.url,
            total_events=args.events,
            concurrent_requests=args.concurrent,
            batch_size=args.batch_size,
            test_duration_seconds=test_duration,
            app_id=args.app_id,
            auth_token=args.auth_token,
            output_file=args.output,
            verbose=args.verbose,
            use_existing_apps=use_existing_apps,
            target_rps=args.target_rps,
            ramp_up_to_target=args.ramp_up,
            ramp_duration_seconds=args.ramp_duration,
            rate_limit_mode=True
        )
    else:
        config = BenchmarkConfig(
            gateway_url=args.url,
            total_events=args.events,
            concurrent_requests=args.concurrent,
            batch_size=args.batch_size,
            test_duration_seconds=args.duration,
            app_id=args.app_id,
            auth_token=args.auth_token,
            output_file=args.output,
            verbose=args.verbose,
            use_existing_apps=use_existing_apps,
            rate_limit_mode=False
        )
    
    # Log configuration
    logger.info(f"🚀 Starting benchmark with configuration:")
    logger.info(f"   App Strategy: {'Random existing apps' if use_existing_apps else f'Single app: {args.app_id}'}")
    if use_existing_apps:
        logger.info(f"   App Pool: {len(EXISTING_APP_IDS)} existing applications")
    
    if config.rate_limit_mode:
        logger.info(f"   Mode: Rate Limited ({config.target_rps} RPS)")
        if config.ramp_up_to_target:
            logger.info(f"   Ramp Up: {config.ramp_duration_seconds}s to reach target")
        logger.info(f"   Duration: {config.test_duration_seconds}s")
    else:
        logger.info(f"   Mode: Traditional Load Test")
        if config.test_duration_seconds:
            logger.info(f"   Duration: {config.test_duration_seconds}s")
        else:
            logger.info(f"   Events: {config.total_events}")
    
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