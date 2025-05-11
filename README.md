# Driving Telemetry System

This project consists of a Kafka producer generating random telemetry data and a Kafka consumer that stores it into HBase.

## Setup

1. Run Docker environment:
   ```
   docker-compose -f docker/docker-compose.yml up -d
   ```

2. Bootstrap Kafka topic and HBase table:
   ```
   ./docker/bootstrap.sh
   ```

3. Build and run both producer and consumer apps via Maven or your IDE.

spark app
--add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED

minikube mount ..Developer/driving-telemetry:/mnt/driving-telemetry

--add-exports java.base/sun.nio.ch=ALL-UNNAMED