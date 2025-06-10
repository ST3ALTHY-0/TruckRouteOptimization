package com.truckoptimization.dto;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class RouteResult {
    @Id
    private int vehicleId;
    private List<Integer> route;
    private long totalDistance;
    private long totalLoad;



    public static void printRouteResults(List<RouteResult> results) {
        System.out.println("Total vehicles used: " + results.size());
        for (RouteResult result : results) {
        System.out.println("Route for Vehicle " + result.getVehicleId() + ":");
        System.out.println("  Route: " + result.getRoute());
        System.out.println("  Total Distance: " + result.getTotalDistance() + " meters");
        System.out.println("  Total Load: " + result.getTotalLoad());
        System.out.println();
    }
}

}