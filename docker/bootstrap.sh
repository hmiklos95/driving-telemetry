#!/bin/bash

echo "Creating Kafka topic: telemetry"
docker exec kafka kafka-topics --create \
  --topic telemetry \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1 \
  --if-not-exists

echo "Waiting for HBase to start..."
sleep 10

echo "Creating HBase table: telemetry_data"
docker exec -it hbase bash -c "echo \"create 'telemetry_data', 'data'\" | hbase shell -n"

echo "Bootstrap finished."


