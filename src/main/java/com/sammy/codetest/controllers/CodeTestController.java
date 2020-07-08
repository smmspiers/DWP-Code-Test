package com.sammy.codetest.controllers;

import com.sammy.codetest.HaversineDistance;
import com.sammy.codetest.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CodeTestController {

    private static final String CITY_API_URL = "https://bpdts-test-app.herokuapp.com/";
    private static final double LONDON_LATITUDE = 51.509865;
    private static final double LONDON_LONGITUDE = -0.118092;

    @GetMapping("London-users")
    public List<User> getLondonUsers() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                CITY_API_URL + "/city/London/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}).getBody();
    }

    @GetMapping("London-users-nearby")
    public List<User> getNearbyLondonUsers() {
        RestTemplate restTemplate = new RestTemplate();
        List<User> users = restTemplate.exchange(
                CITY_API_URL + "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}).getBody();
        return users.stream()
                .filter(user -> HaversineDistance.calculate(user.getLatitude(), user.getLongitude(), LONDON_LATITUDE, LONDON_LONGITUDE) < 50)
                .collect(Collectors.toList());
    }

}
