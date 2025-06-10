package com.truckoptimization.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.hibernate.annotations.SecondaryRow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import com.truckoptimization.orm.LocationService;
import com.truckoptimization.orm.Location;

//helper class to convert a String address into a set of Lat Long cords, this is needed for the distance matrix calculation.
//we use a free api provided by geocode, with limitations of 1 request a second and 5000 requests a day

@Service
public class DistanceToLatLongConvertor {

    @Autowired
    private LocationService locationService;

    public double[] convertAddressToLatLong(String address) {
        try {
            String apiKey = "6840af2b73890362038950ayi7f2852";
            String urlStr = "https://geocode.maps.co/search?q=" +
                    java.net.URLEncoder.encode(address, "UTF-8") +
                    "&api_key=" + apiKey;

            Location location = locationService.checkLocationExists(address).orElse(null);

            if (location != null) {
                return new double[] { location.getLatitude(), location.getLongitude() };
            }

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray arr = new JSONArray(response.toString());
            if (arr.length() > 0) {
                JSONObject obj = arr.getJSONObject(0);
                double lat = Double.parseDouble(obj.getString("lat"));
                double lon = Double.parseDouble(obj.getString("lon"));

                System.out.printf("Lat: %f, Long: %f%n", lat, lon);

                locationService.saveLocation(address, lat, lon);

                try {
                    Thread.sleep(1050);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return new double[] { lat, lon };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
