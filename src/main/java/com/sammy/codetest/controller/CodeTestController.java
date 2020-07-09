package com.sammy.codetest.controller;

import com.sammy.codetest.config.CodeTestConfigurationProperties;
import com.sammy.codetest.exception.ApiError;
import com.sammy.codetest.exception.CityNotFoundException;
import com.sammy.codetest.model.User;
import com.sammy.codetest.service.HaversineDistanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CodeTestController {

    private static final Logger log = LoggerFactory.getLogger(CodeTestController.class);

    private final CodeTestConfigurationProperties config;
    private final HaversineDistanceService distanceService;

    @Autowired
    public CodeTestController(CodeTestConfigurationProperties config, HaversineDistanceService distanceService) {
        this.config = config;
        this.distanceService = distanceService;
    }

    @GetMapping("users/city/{cityName}")
    public List<User> getCityUsers(@PathVariable String cityName) {
        final RestTemplate restTemplate = new RestTemplate();
        final Optional<List<User>> users = Optional.ofNullable(
                restTemplate.exchange(
                        String.format(config.getCityApiUrl() + "/city/%s/users", cityName),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {}).getBody());
        log.info(String.format("Calling external API for all users listed as living in %s", cityName));
        if (users.isEmpty()) {
            throw new CityNotFoundException(cityName);
        }
        return users.get();
    }

    @GetMapping("users/city/{cityName}/nearby")
    public List<User> getNearbyCityUsers(@PathVariable String cityName) {
        final RestTemplate restTemplate = new RestTemplate();
        final Optional<List<User>> users = Optional.ofNullable(
                restTemplate.exchange(
                        config.getCityApiUrl() + "/users",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {}).getBody());
        log.info(String.format("Calling external API for all users living within 50 miles of %s", cityName));
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.get().stream()
                .filter(user -> distanceService.calculateCityDistance(user.getLatitude(), user.getLongitude(), cityName) < 50)
                .collect(Collectors.toList());
    }

    @RestControllerAdvice
    public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(value = CityNotFoundException.class)
        protected ResponseEntity<Object> handleUnknownCity(CityNotFoundException ex, WebRequest request) {
            log.error(String.format("%s is not a recognised city", ex.getCityName()));
            return handleExceptionInternal(
                    ex,
                    new ApiError(ex.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

}
