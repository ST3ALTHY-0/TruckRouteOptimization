package com.truckoptimization;

import java.util.ArrayList;
import java.util.List;

import com.google.ortools.Loader;

import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.util.OptionalBoolean;
import com.google.protobuf.Duration;
import com.google.ortools.constraintsolver.Assignment;
// import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.main;

public class Main {

    // private static final List<double[]> coords = List.of(
    // new double[]{41.718731, -86.900902},
    // new double[]{41.490182, -82.094468},
    // new double[]{41.468269, -82.178230},
    // new double[]{41.367710, -82.107819},
    // new double[]{47.213230, -94.755234},
    // new double[]{41.468088, -82.179062}
    // );

    private static final List<String> addresses = List.of(
        "INDIANAPOLIS, IN",
    "UPPER SANDUSKY, OH",
    "SHEFFIELD LAKE, OH",
    "LORAIN, OH",
    "ELYRIA, OH",
    "WELLINGTON, OH",
    "WOOSTER, OH",
    "NEW PHILADELPHIA, OH",
    "ASHLAND, OH",
    "ORRVILLE, OH",
    "MEDINA, OH",
    "COSHOCTON, OH",
    "WADSWORTH, OH",
    "DOVER, OH",
    "MEDINA, OH",
    "CANTON, OH",
    "AKRON, OH",
    "GALION, OH",
    "ADA, OH",
    "OTTAWA, OH",
    "WAPAKONETA, OH",
    "BLUFFTON, OH",
    "CRIDERSVILLE, OH",
    "BERNE, IN",
    "GREENFIELD, OH",
    "NORTH BALTIMORE, OH",
    "OAK HARBOR, OH",
    "DELTA, OH",
    "PLYMOUTH, OH",
    "LAKEVIEW, OH",
    "BELLEFONTAINE, OH",
    "HURON, OH",
    "CINCINNATI, OH",
    "BELLBROOK, OH",
    "CENTERVILLE, OH",
    "KETTERING, OH",
    "GREENVILLE, OH",
    "EDGERTON, OH",
    "WEST MANSFIELD, OH",
    "NEW CARLISLE, OH",
    "BELLEFONTAINE, OH"
);

    private static final int[] demands = {
        0, //Indianapolis
    1,  // UPPER SANDUSKY, OH
    3,  // SHEFFIELD LAKE, OH
    2,  // LORAIN, OH
    3,  // ELYRIA, OH
    2,  // WELLINGTON, OH
    3,  // WOOSTER, OH
    3,  // NEW PHILADELPHIA, OH
    3,  // ASHLAND, OH
    4,  // ORRVILLE, OH
    4,  // MEDINA, OH
    2,  // COSHOCTON, OH
    4,  // WADSWORTH, OH
    4,  // DOVER, OH
    1,  // MEDINA, OH (44256-8100)
    4,  // CANTON, OH
    1,  // AKRON, OH
    2,  // GALION, OH
    3,  // ADA, OH
    3,  // OTTAWA, OH
    2,  // WAPAKONETA, OH
    4,  // BLUFFTON, OH
    1,  // CRIDERSVILLE, OH
    3,  // BERNE, IN
    2,  // GREENFIELD, OH
    1,  // NORTH BALTIMORE, OH
    2,  // OAK HARBOR, OH
    1,  // DELTA, OH
    2,  // PLYMOUTH, OH
    2,  // LAKEVIEW, OH
    1,  // BELLEFONTAINE, OH
    3,  // HURON, OH
    3,  // CINCINNATI, OH
    1,  // BELLBROOK, OH
    2,  // CENTERVILLE, OH
    0,  // KETTERING, OH
    2,  // GREENVILLE, OH
    1,  // EDGERTON, OH
    1,  // NEW CARLISLE, OH
    1,  // WEST MANSFIELD, OH
    2   // BELLEFONTAINE, OH
};

    public static void main(String[] args) {

        OptimizeRoutes optimizeRoutes = new OptimizeRoutes();
        List<double[]> cords = new ArrayList<>();
        long[][] distanceMatrixCords;

        for (String address : addresses) {
            cords.add(DistanceToLatLongConvertor.convertAddressToLatLong(address));
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            distanceMatrixCords = DistanceMatrixCalculation.getDistanceMatrix(cords);
            DistanceMatrixCalculation.printDistanceMatrix(distanceMatrixCords);
            optimizeRoutes.OptimizeRoutesFrom2DCords(distanceMatrixCords, addresses.size(), demands);
        } catch (Exception e) {
        System.out.println("ERROR: " + e.getMessage());
        e.printStackTrace();
        }

    }

}
