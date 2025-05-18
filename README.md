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

## docker folder
docker build . -t hbase-local:latest
minikube image load hbase-local:latest

minikube image load spring-app:latest

kafka kafka-topics --create \
--topic telemetry \
--bootstrap-server localhost:9092 \
--partitions 1 \
--replication-factor 1 \
--if-not-exists


hbase shell
create 'telemetry_data', 'data'

minikube mount Developer/driving-telemetry/analytics/algorithms/algorithm-speed/target/:/mnt/jars

kubectl delete sparkapp driver-speed

kubectl exec --stdin --tty zookeeper-5c4c8cdd5f-gs6fd -- /bin/bash