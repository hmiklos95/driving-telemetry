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

put 'telemetry_data', 'driver-0_1745503413292', 'data:speed', 123.45
put 'telemetry_data', 'driver-0_1745503413292', 'data:acceleration', 9.81
flush 'telemetry_data'

minikube mount Developer/driving-telemetry/analytics/algorithms/algorithm-speed/target/:/mnt/jars

kubectl delete sparkapp driver-speed

kubectl exec --stdin --tty zookeeper-5c4c8cdd5f-gs6fd -- /bin/bash

mvn clean install spring-boot:repackage
docker build --build-arg JAR_FILE=telemetry/consumer-app/target/consumer-app-1.0.0.jar . -t consumer-app2

minikube image load [app-name image]