package com.truckoptimization;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.google.ortools.Loader;

import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.util.OptionalBoolean;
import com.google.protobuf.Duration;
import com.truckoptimization.dto.RouteResult;
import com.truckoptimization.service.DistanceMatrixCalculation;
import com.truckoptimization.service.DistanceToLatLongConvertor;
import com.truckoptimization.service.OptimizeRoutes;
import com.truckoptimization.service.TestData;
import com.google.ortools.constraintsolver.Assignment;
// import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.main;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
@EnableCaching
public class Main implements CommandLineRunner {

    @Autowired
    private DistanceToLatLongConvertor distanceToLatLongConvertor;

    @Autowired
    private DistanceMatrixCalculation distanceMatrixCalculation;

    @Autowired
    private TestData testData;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        OptimizeRoutes optimizeRoutes = new OptimizeRoutes();
        List<double[]> cords = new ArrayList<>();
        long[][] distanceMatrixCords;
        List<RouteResult> routingResults = new ArrayList<>();

        for (String address : testData.addresses) {
            cords.add(distanceToLatLongConvertor.convertAddressToLatLong(address));
        }

        try {
            distanceMatrixCords = distanceMatrixCalculation.getDistanceMatrix(cords);
            
            distanceMatrixCalculation.printDistanceMatrix(distanceMatrixCords);
            optimizeRoutes.init();
            routingResults = optimizeRoutes.OptimizeRoutesFrom2DCords(distanceMatrixCords, testData.addresses.size(), testData.demands);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        RouteResult.printRouteResults(routingResults);
    }

}
