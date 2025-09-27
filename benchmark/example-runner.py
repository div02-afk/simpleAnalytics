#!/usr/bin/env python3
"""
Quick Example Runner for Event Ingestion Benchmark
Demonstrates various benchmarking scenarios
"""

import asyncio
import subprocess
import sys
import os
from pathlib import Path

def run_command(cmd, description):
    """Run a command and display results"""
    print(f"\n{'='*60}")
    print(f"🧪 {description}")
    print(f"{'='*60}")
    print(f"Command: {' '.join(cmd)}")
    print()
    
    try:
        result = subprocess.run(cmd, capture_output=False, text=True)
        if result.returncode == 0:
            print(f"✅ {description} completed successfully")
        else:
            print(f"❌ {description} failed with exit code {result.returncode}")
        return result.returncode == 0
    except Exception as e:
        print(f"❌ Error running {description}: {e}")
        return False

def main():
    """Run example benchmark scenarios"""
    script_dir = Path(__file__).parent
    benchmark_script = script_dir / "event-ingestion-benchmark.py"
    
    if not benchmark_script.exists():
        print("❌ Benchmark script not found!")
        sys.exit(1)
    
    print("🚀 Event Ingestion Benchmark Examples")
    print("This script demonstrates various benchmarking scenarios")
    print()
    
    # Check if Python is available
    try:
        subprocess.run([sys.executable, "--version"], check=True, capture_output=True)
        python_cmd = sys.executable
    except:
        print("❌ Python not available")
        sys.exit(1)
    
    scenarios = [
        {
            "name": "Quick Validation Test",
            "description": "Fast test with 100 events to validate setup",
            "cmd": [python_cmd, str(benchmark_script), "--events", "100", "--concurrent", "10", "--batch-size", "5", "--verbose"]
        },
        {
            "name": "Standard Load Test",
            "description": "Normal load test with 1000 events",
            "cmd": [python_cmd, str(benchmark_script), "--events", "1000", "--concurrent", "50", "--batch-size", "10"]
        },
        {
            "name": "High Concurrency Test",
            "description": "Test with high concurrent requests",
            "cmd": [python_cmd, str(benchmark_script), "--events", "2000", "--concurrent", "100", "--batch-size", "20"]
        },
        {
            "name": "Duration-based Test",
            "description": "Run benchmark for 60 seconds",
            "cmd": [python_cmd, str(benchmark_script), "--duration", "60", "--concurrent", "30", "--batch-size", "10"]
        }
    ]
    
    print("Available test scenarios:")
    for i, scenario in enumerate(scenarios, 1):
        print(f"  {i}. {scenario['name']} - {scenario['description']}")
    
    print(f"  {len(scenarios) + 1}. Run all scenarios")
    print("  0. Exit")
    print()
    
    while True:
        try:
            choice = input("Select a scenario to run (0-{}): ".format(len(scenarios) + 1))
            choice_num = int(choice)
            
            if choice_num == 0:
                print("👋 Goodbye!")
                break
            elif choice_num == len(scenarios) + 1:
                # Run all scenarios
                print("\n🏃 Running all benchmark scenarios...")
                success_count = 0
                for scenario in scenarios:
                    if run_command(scenario["cmd"], scenario["name"]):
                        success_count += 1
                
                print(f"\n📊 Summary: {success_count}/{len(scenarios)} scenarios completed successfully")
                break
            elif 1 <= choice_num <= len(scenarios):
                scenario = scenarios[choice_num - 1]
                run_command(scenario["cmd"], scenario["name"])
                
                continue_choice = input("\nRun another scenario? (y/n): ").lower()
                if continue_choice != 'y':
                    break
            else:
                print("❌ Invalid choice. Please select 0-{}".format(len(scenarios) + 1))
                
        except ValueError:
            print("❌ Please enter a valid number")
        except KeyboardInterrupt:
            print("\n👋 Goodbye!")
            break

if __name__ == "__main__":
    main()