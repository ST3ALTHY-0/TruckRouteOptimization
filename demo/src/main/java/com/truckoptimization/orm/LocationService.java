package com.truckoptimization.orm;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location getOrSaveLocation(String address, double lat, double lng) {
        return locationRepository.findByAddress(address)
                .orElseGet(() -> {
                    Location loc = new Location();
                    loc.setAddress(address);
                    loc.setLatitude(lat);
                    loc.setLongitude(lng);
                    return locationRepository.save(loc);
                });

    }

    public Optional<Location> checkLocationExists(String address) {
        return locationRepository.findByAddress(address);
    }

    public Location saveLocation(String address, double lat, double lng) {
        Location loc = new Location();
        loc.setAddress(address);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        return locationRepository.save(loc);
    }

}
