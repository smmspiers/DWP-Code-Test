package com.sammy.codetest.service;

import com.sammy.codetest.config.City;
import com.sammy.codetest.config.CodeTestConfigurationProperties;
import com.sammy.codetest.exception.UnknownCityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HaversineDistanceService {

    private static final double RADIUS_OF_EARTH = 3958.8;

    private final CodeTestConfigurationProperties config;

    @Autowired
    public HaversineDistanceService(CodeTestConfigurationProperties config) {
        this.config = config;
    }

    private double calculateDistance(double lat1, double long1, double lat2, double long2) {
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(long1 - long2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        final double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        final double c = 2 * Math.asin(Math.sqrt(a));
        return RADIUS_OF_EARTH * c;
    }

    public double calculateCityDistance(double lat1, double long1, String cityName) {
        final Optional<City> city = config.getCities().stream()
                .filter(c -> cityName.equals(c.getName()))
                .findFirst();
        if (city.isEmpty()) {
            throw new UnknownCityException();
        }
        return calculateDistance(lat1, long1, city.get().getLatitude(), city.get().getLongitude());
    }

}
