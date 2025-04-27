package com.example.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 1)
    public void sendTelemetry() throws JsonProcessingException {
        TelemetryData data = new TelemetryData();
        data.setDriverId("driver-" + (new Random().nextInt(5)));
        data.setSpeed(30 + Math.random() * 100);
        data.setAcceleration(Math.random() * 5);
        data.setBrake(Math.random() * 3);
        data.setTimestamp(System.currentTimeMillis());

        String json = objectMapper.writeValueAsString(data);
        kafkaTemplate.send("telemetry", data.getDriverId(), json);
    }
}
