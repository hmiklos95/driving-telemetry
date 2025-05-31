package com.example.consumer;

import java.io.IOException;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final KafkaTelemetryConsumer kafkaTelemetryConsumer;
    @GetMapping
    public void submit() throws IOException {

        for (int i = 0; i < 1000000; i++) {
            TelemetryData data = new TelemetryData();
            data.setDriverId("driver-" + (new Random().nextInt(5)));
            data.setSpeed(30 + Math.random() * 100);
            data.setAcceleration(Math.random() * 5);
            data.setBrake(Math.random() * 3);
            data.setTimestamp(System.currentTimeMillis());

            kafkaTelemetryConsumer.saveToHBase(data);
        }

    }
}
