package com.truckoptimization.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "routing")
@Data
public class RoutingConfig {
    private int maxDistanceMeters;
    private int optimalDistanceMeters;
    private int maxNumberTrucks;
    private int vehicleCapacity;
}