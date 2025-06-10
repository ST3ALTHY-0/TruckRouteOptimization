package com.truckoptimization.service;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

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
import com.truckoptimization.dto.RouteResult;

import jakarta.annotation.PostConstruct;

import com.google.ortools.constraintsolver.main;

@Service
public class OptimizeRoutes {

    private static final int MAX_DISTANCE_METERS = 1_000_000; // for now needs to be at least 900000 so that we can get
                                                              // to
                                                              // ohio and back
    private static final int OPTIMAL_DISTANCE_METERS = 600_000;
    private static final int MAX_DISTANCE_METERS_TEST = 1_500_000;
    private static final int MAX_NUMBER_TRUCKS = 15;
    private static final int VEHICLE_CAPACITY = 20;
    private static final int SECONDS_TO_CALCULATE = 10; // how long the model will spend trying to find the optimal
                                                        // solution
    private static final int COST_OF_ADDING_VEHICLE = 100_000; // discourages model from adding vehicles
    private static final long PENALTY_OF_MISSING_LOCATION = 100_000_000;
    private static final long PENALTY_PER_METER_OVER_OR_UNDER = 50; // discourages model from longer routes

    @PostConstruct
    public void init() {
        Loader.loadNativeLibraries();
    }

    public List<RouteResult> OptimizeRoutesFrom2DCords(long[][] distanceMatrixCords, int numLocations, int[] demands) {
        try {

            // depo = 0 sets the first first elements in the distanceMatrixCords, and
            // demands to the pick-up drop off location
            int depot = 0;
            int numVehicles = MAX_NUMBER_TRUCKS;

            // sets all the vehicles to have the same capacity
            long[] vehicleCapacities = new long[numVehicles];
            Arrays.fill(vehicleCapacities, VEHICLE_CAPACITY);

            // Create the routing index manager and routing model
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

            addConstraints(numVehicles, vehicleCapacities, demandCallbackIndex, routing, transitCallbackIndex);

            // Set up pickup and delivery pairs if you want to specify 1 or multiple pick up
            // and drop of points along the route for the trucks
            // for (int i = 1; i < numLocations; i++) {
            // if (demands[i] > 0) {
            // long pickupIndex = manager.nodeToIndex(depot);
            // long deliveryIndex = manager.nodeToIndex(i);

            // routing.addPickupAndDelivery(pickupIndex, deliveryIndex);
            // routing.solver().addConstraint(
            // routing.solver().makeEquality(routing.vehicleVar(pickupIndex),
            // routing.vehicleVar(deliveryIndex)));
            // routing.solver().addConstraint(
            // routing.solver().makeLessOrEqual(
            // capacityDimension.cumulVar(pickupIndex),
            // capacityDimension.cumulVar(deliveryIndex)));
            // }
            // }

            RoutingSearchParameters searchParameters = configureSearchParams();

            System.out.println("Demands: " + Arrays.toString(demands));
            System.out.println("Capacities: " + Arrays.toString(vehicleCapacities));
            int totalDemand = Arrays.stream(demands).sum();
            long totalCapacity = Arrays.stream(vehicleCapacities).sum();
            System.out.println("Total demand: " + totalDemand + ", Total capacity: " + totalCapacity);

            Assignment solution = routing.solveWithParameters(searchParameters);
            return extractSolution(solution, numVehicles, manager, routing, distanceMatrixCords, demands);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;

    }

    private RoutingSearchParameters configureSearchParams() {
        return main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                .setTimeLimit(Duration.newBuilder().setSeconds(SECONDS_TO_CALCULATE).build())
                .build();
    }

    private void addConstraints(int numVehicles, long[] vehicleCapacities, int demandCallbackIndex,
            RoutingModel routing, int transitCallbackIndex) {
        routing.addDimensionWithVehicleCapacity(
                demandCallbackIndex,
                0,
                vehicleCapacities,
                true,
                "Capacity");

        RoutingDimension capacityDimension = routing.getMutableDimension("Capacity");

        // have max miles traveled for any one truck
        routing.addDimension(
                transitCallbackIndex,
                0,
                MAX_DISTANCE_METERS,
                true,
                "Distance");

        RoutingDimension distanceDimension = routing.getMutableDimension("Distance");

        // TODO: could add soft lower bound
        // penalize adding more vehicles (minimizing vehicles needed), and add max
        // distance any one truck can go, and a penalty for longer distances
        BoundCost maxBoundCost = new BoundCost(MAX_DISTANCE_METERS, PENALTY_PER_METER_OVER_OR_UNDER);
        BoundCost optimalBoundCost = new BoundCost(OPTIMAL_DISTANCE_METERS, PENALTY_PER_METER_OVER_OR_UNDER);
        BoundCost minDistanceBoundCost = new BoundCost(OPTIMAL_DISTANCE_METERS, PENALTY_PER_METER_OVER_OR_UNDER);

        for (int vehicleId = 0; vehicleId < numVehicles; vehicleId++) {

            distanceDimension.setCumulVarSoftLowerBound(routing.end(vehicleId), OPTIMAL_DISTANCE_METERS,
                    PENALTY_PER_METER_OVER_OR_UNDER);

            distanceDimension.setCumulVarSoftUpperBound(routing.end(vehicleId), MAX_DISTANCE_METERS,
                    PENALTY_PER_METER_OVER_OR_UNDER);

            routing.setFixedCostOfVehicle(COST_OF_ADDING_VEHICLE, vehicleId);
        }

    }

    private List<RouteResult> extractSolution(Assignment solution, int numVehicles, RoutingIndexManager manager,
            RoutingModel routing, long[][] distanceMatrixCords, int[] demands) {
        List<RouteResult> results = new ArrayList<>();
        // Print routes with distance between each step
        if (solution != null) {

            for (int vehicleId = 0; vehicleId < numVehicles; vehicleId++) {
                long index = routing.start(vehicleId);
                long nextIndex = solution.value(routing.nextVar(index));

                if (routing.isEnd(nextIndex)) {
                    continue;
                }

                List<Integer> route = new ArrayList<>();
                long routeLoad = 0;
                long routeDistance = 0;

                long previousIndex = index;
                index = nextIndex;

                route.add(manager.indexToNode(previousIndex));

                while (!routing.isEnd(index)) {
                    int nodeIndex = manager.indexToNode(index);
                    routeLoad += demands[nodeIndex];
                    route.add(nodeIndex);

                    int fromNode = manager.indexToNode(previousIndex);
                    int toNode = manager.indexToNode(index);
                    routeDistance += distanceMatrixCords[fromNode][toNode];

                    previousIndex = index;
                    index = solution.value(routing.nextVar(index));
                }

                // Add depot at end
                route.add(manager.indexToNode(index));
                // Add last leg back to depot distance
                int fromNode = manager.indexToNode(previousIndex);
                int toNode = manager.indexToNode(index);
                routeDistance += distanceMatrixCords[fromNode][toNode];

                results.add(new RouteResult(vehicleId, route, routeDistance, routeLoad));
            }
            return results;
        } else {
            System.out.println("No solution found.");
            return null;
        }

    }

}
