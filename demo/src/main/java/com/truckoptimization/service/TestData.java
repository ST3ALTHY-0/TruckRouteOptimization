package com.truckoptimization.service;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TestData {

    public final List<String> addresses = List.of(
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
            "BELLEFONTAINE, OH");

    public final int[] demands = {
            0, // Indianapolis
            1, // UPPER SANDUSKY, OH
            3, // SHEFFIELD LAKE, OH
            2, // LORAIN, OH
            3, // ELYRIA, OH
            2, // WELLINGTON, OH
            3, // WOOSTER, OH
            3, // NEW PHILADELPHIA, OH
            3, // ASHLAND, OH
            4, // ORRVILLE, OH
            4, // MEDINA, OH
            2, // COSHOCTON, OH
            4, // WADSWORTH, OH
            4, // DOVER, OH
            1, // MEDINA, OH (44256-8100)
            4, // CANTON, OH
            1, // AKRON, OH
            2, // GALION, OH
            3, // ADA, OH
            3, // OTTAWA, OH
            2, // WAPAKONETA, OH
            4, // BLUFFTON, OH
            1, // CRIDERSVILLE, OH
            3, // BERNE, IN
            2, // GREENFIELD, OH
            1, // NORTH BALTIMORE, OH
            2, // OAK HARBOR, OH
            1, // DELTA, OH
            2, // PLYMOUTH, OH
            2, // LAKEVIEW, OH
            1, // BELLEFONTAINE, OH
            3, // HURON, OH
            3, // CINCINNATI, OH
            1, // BELLBROOK, OH
            2, // CENTERVILLE, OH
            0, // KETTERING, OH
            2, // GREENVILLE, OH
            1, // EDGERTON, OH
            1, // NEW CARLISLE, OH
            1, // WEST MANSFIELD, OH
            2 // BELLEFONTAINE, OH
    };

    public final int[] pickupAndDelivery = {
            0, // Indianapolis
            1, // UPPER SANDUSKY, OH
            3, // SHEFFIELD LAKE, OH
            2, // LORAIN, OH
            3, // ELYRIA, OH
            2, // WELLINGTON, OH
            3, // WOOSTER, OH
            3, // NEW PHILADELPHIA, OH
            3, // ASHLAND, OH
            4, // ORRVILLE, OH
            4, // MEDINA, OH
            2, // COSHOCTON, OH
            4, // WADSWORTH, OH
            4, // DOVER, OH
            1, // MEDINA, OH (44256-8100)
            4, // CANTON, OH
            1, // AKRON, OH
            2, // GALION, OH
            3, // ADA, OH
            3, // OTTAWA, OH
            2, // WAPAKONETA, OH
            4, // BLUFFTON, OH
            1, // CRIDERSVILLE, OH
            3, // BERNE, IN
            2, // GREENFIELD, OH
            1, // NORTH BALTIMORE, OH
            2, // OAK HARBOR, OH
            1, // DELTA, OH
            2, // PLYMOUTH, OH
            2, // LAKEVIEW, OH
            1, // BELLEFONTAINE, OH
            3, // HURON, OH
            3, // CINCINNATI, OH
            1, // BELLBROOK, OH
            2, // CENTERVILLE, OH
            0, // KETTERING, OH
            2, // GREENVILLE, OH
            1, // EDGERTON, OH
            1, // NEW CARLISLE, OH
            1, // WEST MANSFIELD, OH
            2 // BELLEFONTAINE, OH
    };

    public final List<double[]> coordinates = List.of(
            new double[] { 39.768333, -86.158350 },
            new double[] { 40.827279, -83.281309 },
            new double[] { 41.487540, -82.101537 },
            new double[] { 41.263355, -82.173475 },
            new double[] { 41.367319, -82.107358 },
            new double[] { 41.168587, -82.217568 },
            new double[] { 40.798098, -81.939773 },
            new double[] { 40.489787, -81.445671 },
            new double[] { 40.788737, -82.236039 },
            new double[] { 40.843666, -81.764021 },
            new double[] { 41.100076, -81.938252 },
            new double[] { 40.290568, -81.927144 },
            new double[] { 41.025610, -81.729852 },
            new double[] { 40.521338, -81.474148 },
            new double[] { 41.100076, -81.938252 },
            new double[] { 40.798546, -81.374951 },
            new double[] { 41.083064, -81.518485 },
            new double[] { 40.733795, -82.789647 },
            new double[] { 38.839894, -83.505170 },
            new double[] { 41.542138, -83.213342 },
            new double[] { 40.567827, -84.193559 },
            new double[] { 40.893446, -83.891785 },
            new double[] { 40.654217, -84.150781 },
            new double[] { 40.657824, -84.951912 },
            new double[] { 39.352008, -83.382693 },
            new double[] { 41.182830, -83.678267 },
            new double[] { 41.512773, -83.146578 },
            new double[] { 41.572573, -84.005913 },
            new double[] { 40.995286, -82.665429 },
            new double[] { 40.484772, -83.922995 },
            new double[] { 40.361164, -83.759656 },
            new double[] { 41.130530, -82.591828 },
            new double[] { 39.101454, -84.512460 },
            new double[] { 39.635840, -84.070177 },
            new double[] { 39.628393, -84.159382 },
            new double[] { 39.689504, -84.168827 },
            new double[] { 40.102391, -84.633299 },
            new double[] { 41.448718, -84.748039 },
            new double[] { 40.402000, -83.545205 },
            new double[] { 39.936170, -84.025491 },
            new double[] { 40.361164, -83.759656 });
    
}
