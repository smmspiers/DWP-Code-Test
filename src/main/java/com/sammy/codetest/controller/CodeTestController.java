package com.sammy.codetest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sammy.codetest.exception.UnknownCityException;
import com.sammy.codetest.service.HaversineDistanceService;
import com.sammy.codetest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CodeTestController {

    private static final String CITY_API_URL = "https://bpdts-test-app.herokuapp.com/";

    private final HaversineDistanceService distanceService;

    @Autowired
    public CodeTestController(HaversineDistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @GetMapping("users/city/{cityName}")
    public List<User> getCityUsers(@PathVariable String cityName) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                String.format(CITY_API_URL + "/city/%s/users", cityName),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}).getBody();
    }

    @GetMapping("users/city/{cityName}/nearby")
    public List<User> getNearbyLondonUsers(@PathVariable String cityName) {
        final RestTemplate restTemplate = new RestTemplate();
        final List<User> users = restTemplate.exchange(
                CITY_API_URL + "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}).getBody();
        return users.stream()
                .filter(user -> distanceService.calculateCityDistance(user.getLatitude(), user.getLongitude(), cityName) < 50)
                .collect(Collectors.toList());
    }

//    @ControllerAdvice
//    public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
//
//        @ExceptionHandler(value = UnknownCityException.class)
//        protected ResponseEntity<Object> handleUnknownCity(
//                UnknownCityException ex,
//                WebRequest request) {
//            logger.info(ex);
//            return handleExceptionInternal(ex, new ErrorResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//        }
//    }
}
