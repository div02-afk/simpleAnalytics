# Simple Analytics - Maven Service Runner
# Usage examples for the parent POM file

## 🚀 Quick Start Commands

### Build all services
```bash
mvn clean compile
```

### Build and package all services
```bash
mvn clean package
```

### Start Docker services (Kafka, ClickHouse, Redis, etc.)
```bash
mvn clean initialize -Pdocker-start
```

### Stop Docker services
```bash
mvn clean initialize -Pdocker-stop
```

## 🎯 Running Individual Services

### Run Gateway service only (port 8001)
```bash
mvn clean -pl Gateway spring-boot:run
```

### Run EventConsumer service only (port 8000)
```bash
mvn clean -pl EventConsumer spring-boot:run
```

### Run TenetService only (port 8002)
```bash
mvn clean -pl TenetService spring-boot:run
```

## 🏃 Running All Services

### Option 1: Run all services in separate terminals (Windows)
```bash
mvn clean initialize -Prun-all-services
```

### Option 2: Manual approach (recommended for development)
Open 3 separate terminals and run:

**Terminal 1 - Gateway:**
```bash
mvn -pl Gateway spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8001"
```

**Terminal 2 - EventConsumer:**
```bash
mvn -pl EventConsumer spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8000"
```

**Terminal 3 - TenetService:**
```bash
mvn -pl TenetService spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8002"
```

## 🔧 Development Profiles

### Run with development profile (debug logging)
```bash
mvn -pl Gateway spring-boot:run -Pdev
```

### Run with production profile
```bash
mvn -pl EventConsumer spring-boot:run -Pprod
```

## 🐳 Complete Startup Sequence

1. **Start infrastructure services:**
   ```bash
   mvn clean initialize -Pdocker-start
   ```

2. **Wait 30 seconds for services to be ready**

3. **Start application services in order:**
   ```bash
   # Terminal 1
   mvn -pl TenetService spring-boot:run
   
   # Terminal 2  
   mvn -pl EventConsumer spring-boot:run
   
   # Terminal 3
   mvn -pl Gateway spring-boot:run
   ```

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run integration tests
```bash
mvn verify -Ptest
```

### Run benchmark after services are up
```bash
cd benchmark
python event-ingestion-benchmark.py --url http://localhost:8001 --events 100 --verbose
```

## 📋 Service Endpoints

- **Gateway**: http://localhost:8001
  - Event endpoint: http://localhost:8001/event
  - Health: http://localhost:8001/actuator/health

- **EventConsumer**: http://localhost:8000  
  - Health: http://localhost:8000/api/health/events

- **TenetService**: http://localhost:8002
  - Health: http://localhost:8002/actuator/health

## 🛠️ Troubleshooting

### Check if services are running
```bash
# Check processes
netstat -an | findstr :8001  # Gateway
netstat -an | findstr :8000  # EventConsumer  
netstat -an | findstr :8002  # TenetService

# Check Docker services
docker-compose -f common/docker-compose.yml ps
```

### View logs
```bash
# Add logging configuration to see more details
mvn -pl Gateway spring-boot:run -Dlogging.level.com.simpleAnalytics=DEBUG
```

### Clean and rebuild
```bash
mvn clean package -DskipTests
```

## 🎯 Common Maven Commands

| Command | Description |
|---------|-------------|
| `mvn clean` | Clean build artifacts |
| `mvn compile` | Compile all modules |
| `mvn package` | Build JAR files |
| `mvn install` | Install to local repository |
| `mvn dependency:tree` | Show dependency tree |
| `mvn versions:display-plugin-updates` | Check for plugin updates |
| `mvn help:active-profiles` | Show active profiles |

## 📝 Notes

- The parent POM manages dependencies and versions for all modules
- Each service can be built and run independently  
- Docker services must be running for the application to work properly
- Use different terminals for each service to see individual logs
- Gateway depends on EventConsumer and TenetService being available