package com.truckoptimization;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.BoundCost;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.protobuf.Duration;
import com.google.ortools.constraintsolver.main;

public class OptimizeRoutes {

    private static final int MAX_DISTANCE_METERS = 1000000; //for now needs to be at least 900000 so that we can get to ohio and back
    private static final int MAX_DISTANCE_METERS_TEST = 2000000; // ~1243 miles
    private static final int MAX_NUMBER_TRUCKS = 20;
    private static final int VEHICLE_CAPACITY = 10;
    private static final int SECONDS_TO_CALCULATE = 10; //how long the model will spend trying to find the optimal solution
    private static final int COST_OF_ADDING_VEHICLE = 10000; //discourages model from adding vehicles
    private static final long PENALTY_OF_MISSING_LOCATION = 100_000_000;
    private static final long PENALTY_PER_METER_OVER = 1000; //discourages model from longer routes

    public Assignment OptimizeRoutesFrom2DCords(long[][] distanceMatrixCords, int numLocations, int[] demands) {

        try {
            Loader.loadNativeLibraries();

            // depo = 0 sets the first first elements in the distanceMatrixCords, and
            // demands to the pick-up drop off location
            int depot = 0;

            int numVehicles = MAX_NUMBER_TRUCKS;

            // sets all the vehicles to have the same capacity
            long[] vehicleCapacities = new long[numVehicles];
            for (int i = 0; i < numVehicles; i++) {
                vehicleCapacities[i] = VEHICLE_CAPACITY;
            }

            RoutingIndexManager manager = new RoutingIndexManager(numLocations, numVehicles, depot);
            RoutingModel routing = new RoutingModel(manager);

            // Distance callback
            final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                int fromNode = manager.indexToNode(fromIndex);
                int toNode = manager.indexToNode(toIndex);
                return distanceMatrixCords[fromNode][toNode];
            });

            routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

            // Add Capacity constraint
            final int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
                int fromNode = manager.indexToNode(fromIndex);
                return demands[fromNode];
            });

            routing.addDimensionWithVehicleCapacity(
                    demandCallbackIndex,
                    0,
                    vehicleCapacities,
                    true,
                    "Capacity");

            // have max miles traveled for any one truck
            routing.addDimension(
                    transitCallbackIndex,
                    0,
                    MAX_DISTANCE_METERS,
                    true,
                    "Distance");

            RoutingDimension distanceDimension = routing.getMutableDimension("Distance");

            // add penalty for missing location | to enforce going to every location, no mater what, comment out code, may result in no solution
            // for (int i = 1; i < numLocations; i++) {
            // routing.addDisjunction(new long[] { manager.nodeToIndex(i) },
            // PENALTY_OF_MISSING_LOCATION);
            // }

            //penalize adding more vehicles (minimizing vehicles needed), and add max distance any one truck can go, and a penalty for longer distances
            BoundCost boundCost = new BoundCost(MAX_DISTANCE_METERS, PENALTY_PER_METER_OVER);
            for (int vehicleId = 0; vehicleId < numVehicles; vehicleId++) {
            routing.setFixedCostOfVehicle(COST_OF_ADDING_VEHICLE, vehicleId);
            distanceDimension.setSoftSpanUpperBoundForVehicle(boundCost, vehicleId);
            }

            RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                    .toBuilder()
                    .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                    .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                    .setTimeLimit(Duration.newBuilder().setSeconds(SECONDS_TO_CALCULATE).build())
                    .build();

            System.out.println("Demands: " + Arrays.toString(demands));
            System.out.println("Capacities: " + Arrays.toString(vehicleCapacities));
            int totalDemand = Arrays.stream(demands).sum();
            long totalCapacity = Arrays.stream(vehicleCapacities).sum();
            System.out.println("Total demand: " + totalDemand + ", Total capacity: " + totalCapacity);

            Assignment solution = routing.solveWithParameters(searchParameters);

            // Print routes with distance between each step
            if (solution != null) {
                for (int vehicleId = 0; vehicleId < numVehicles; vehicleId++) {
                    System.out.print("Route for Vehicle " + vehicleId + ": ");
                    long index = routing.start(vehicleId);
                    long routeLoad = 0;
                    long routeDistance = 0;

                    long previousIndex = index;
                    index = solution.value(routing.nextVar(index));

                    while (!routing.isEnd(index)) {
                        int nodeIndex = manager.indexToNode(index);
                        routeLoad += demands[nodeIndex];
                        System.out.print(nodeIndex + " (load " + routeLoad + ") -> ");

                        int fromNode = manager.indexToNode(previousIndex);
                        int toNode = manager.indexToNode(index);
                        routeDistance += distanceMatrixCords[fromNode][toNode];

                        previousIndex = index;
                        index = solution.value(routing.nextVar(index));
                    }

                    System.out.println(manager.indexToNode(index)); // print depot at end

                    // Add last leg back to depot distance
                    int fromNode = manager.indexToNode(previousIndex);
                    int toNode = manager.indexToNode(index);
                    routeDistance += distanceMatrixCords[fromNode][toNode];

                    System.out.println("Total distance: " + routeDistance + " meters");
                }
                return solution;
            } else {
                System.out.println("No solution found.");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;

    }
}
