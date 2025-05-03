package com.example.producer;

import lombok.Data;

@Data
public class TelemetryData {
    private String driverId;
    private double speed;
    private double acceleration;
    private double brake;
    private long timestamp;

    // Getters and Setters
}
