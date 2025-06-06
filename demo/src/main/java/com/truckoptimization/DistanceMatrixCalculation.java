package com.truckoptimization;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DistanceMatrixCalculation {

    // Your OpenRouteService API key
    private static final String ORS_API_KEY = "5b3ce3597851110001cf624895038db6de6141738ee1b465d1a5b9da";
    private static final String MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/driving-car";

    /**
     * Sends coordinates to OpenRouteService and retrieves a distance matrix.
     *
     * @param coordinates A list of [latitude, longitude] arrays.
     * @return A 2D long[][] distance matrix in meters.
     * @throws Exception if the HTTP request fails or the response is invalid.
     */
    public static long[][] getDistanceMatrix(List<double[]> coordinates) throws Exception {
        int size = coordinates.size();
        long[][] matrix = new long[size][size];

        JSONArray locations = new JSONArray();
        for (double[] coord : coordinates) {
            JSONArray point = new JSONArray();
            point.put(coord[1]); // longitude
            point.put(coord[0]); // latitude
            locations.put(point);
        }

        JSONObject requestBody = new JSONObject();

        requestBody.put("locations", locations);
        requestBody.put("metrics", new JSONArray().put("distance")); // Can add "duration"
        requestBody.put("units", "m"); // "km" or "mi" also supported

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MATRIX_URL))
                .header("Authorization", ORS_API_KEY)
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

                
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("ORS API error: " + response.body());
        }

        //System.out.println("Raw ORS Response:\n" + response.body());


        JSONObject json = new JSONObject(response.body());
        JSONArray distances = json.getJSONArray("distances");

        for (int i = 0; i < size; i++) {
            JSONArray row = distances.getJSONArray(i);
            for (int j = 0; j < size; j++) {
                if (!row.isNull(j)) {
                    matrix[i][j] = row.getLong(j);
                } else {
                    matrix[i][j] = Long.MAX_VALUE; // or a large penalty like 999999999
                }
            }
        }

        return matrix;
    }

    public static long[][] buildHaversineDistanceMatrix(List<double[]> coordinates) {
        int size = coordinates.size();
        long[][] distanceMatrix = new long[size][size];

        for (int i = 0; i < size; i++) {
            double[] from = coordinates.get(i);
            for (int j = 0; j < size; j++) {
                double[] to = coordinates.get(j);
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    distanceMatrix[i][j] = haversineDistance(from[0], from[1], to[0], to[1]);
                }
            }
        }
        return distanceMatrix;
    }

    private static long haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c);
    }

    private static long vincentyDistance(double lat1, double lon1, double lat2, double lon2) {
    final double a = 6378137;
    final double f = 1 / 298.257223563; 
    final double b = 6356752.314245;

    double φ1 = Math.toRadians(lat1);
    double φ2 = Math.toRadians(lat2);
    double L = Math.toRadians(lon2 - lon1);
    double U1 = Math.atan((1 - f) * Math.tan(φ1));
    double U2 = Math.atan((1 - f) * Math.tan(φ2));
    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

    double λ = L, λP, iterLimit = 100;
    double sinλ, cosλ, sinσ, cosσ, σ, sinα, cosSqα, cos2σm, C;
    do {
        sinλ = Math.sin(λ);
        cosλ = Math.cos(λ);
        sinσ = Math.sqrt((cosU2 * sinλ) * (cosU2 * sinλ)
                + (cosU1 * sinU2 - sinU1 * cosU2 * cosλ)
                * (cosU1 * sinU2 - sinU1 * cosU2 * cosλ));
        if (sinσ == 0) return 0;
        cosσ = sinU1 * sinU2 + cosU1 * cosU2 * cosλ;
        σ = Math.atan2(sinσ, cosσ);
        sinα = cosU1 * cosU2 * sinλ / sinσ;
        cosSqα = 1 - sinα * sinα;
        cos2σm = cosσ - 2 * sinU1 * sinU2 / cosSqα;
        if (Double.isNaN(cos2σm)) cos2σm = 0;
        C = f / 16 * cosSqα * (4 + f * (4 - 3 * cosSqα));
        λP = λ;
        λ = L + (1 - C) * f * sinα
                * (σ + C * sinσ * (cos2σm + C * cosσ
                * (-1 + 2 * cos2σm * cos2σm)));
    } while (Math.abs(λ - λP) > 1e-12 && --iterLimit > 0);

    if (iterLimit == 0) return -1;

    double uSq = cosSqα * (a * a - b * b) / (b * b);
    double A = 1 + uSq / 16384
            * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
    double B = uSq / 1024
            * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
    double Δσ = B * sinσ * (cos2σm + B / 4
            * (cosσ * (-1 + 2 * cos2σm * cos2σm)
            - B / 6 * cos2σm * (-3 + 4 * sinσ * sinσ)
            * (-3 + 4 * cos2σm * cos2σm)));

    double s = b * A * (σ - Δσ);
    return Math.round(s);
}


    public static void printDistanceMatrix(long[][] matrix){
        System.out.println("Distance Matrix:");
        int size = matrix.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

}
