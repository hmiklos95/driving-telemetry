package com.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaTelemetryConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Connection connection;

    public KafkaTelemetryConsumer(HbaseProperties hbaseProperties) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", hbaseProperties.getZookeeperQuorum());
        config.set("hbase.zookeeper.property.clientPort", hbaseProperties.getZookeeperClientPort());
        config.set("zookeeper.znode.parent", hbaseProperties.getZnodeParent());

        this.connection = ConnectionFactory.createConnection(config);
    }

    //@KafkaListener(topics = "telemetry", groupId = "telemetry-group")
    public void listen(String message) {
        try {
            TelemetryData data = objectMapper.readValue(message, TelemetryData.class);
            saveToHBase(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToHBase(TelemetryData data) throws IOException {
        Table table = connection.getTable(TableName.valueOf("telemetry_data"));
        String rowKey = data.getTimestamp() + "_" + data.getDriverId();

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("speed"), Bytes.toBytes(data.getSpeed()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("acceleration"), Bytes.toBytes(data.getAcceleration()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("brake"), Bytes.toBytes(data.getBrake()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("timestamp"), Bytes.toBytes(data.getTimestamp()));

        table.put(put);
        table.close();
    }
}
