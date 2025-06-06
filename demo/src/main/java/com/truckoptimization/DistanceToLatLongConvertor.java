package com.truckoptimization;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class DistanceToLatLongConvertor {

    //////////limit 1/sec and 5000 a day/////////////////

    /*key: 6840af2b73890362038950ayi7f2852

    address: https://geocode.maps.co/search?q=address&api_key=6840af2b73890362038950ayi7f2852

    return data: [{"place_id":329372438,"licence":"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright","osm_type":"node","osm_id":8983863450,"boundingbox":["41.3577445","41.3777445","-82.1164187","-82.0964187"],"lat":"41.3677445","lon":"-82.1064187","display_name":"Downtown Elyria, Elyria, Lorain County, Ohio, 44035, United States","class":"place","type":"neighbourhood","importance":0.6700099999999999}]
    
    */

    public static double[] convertAddressToLatLong(String address) {
        try {
            String apiKey = "6840af2b73890362038950ayi7f2852";
            String urlStr = "https://geocode.maps.co/search?q=" + 
                java.net.URLEncoder.encode(address, "UTF-8") + 
                "&api_key=" + apiKey;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
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

                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
