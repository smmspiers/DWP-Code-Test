package com.sammy.codetest.controller;

import com.sammy.codetest.exception.ErrorResponse;
import com.sammy.codetest.exception.CityNotFoundException;
import com.sammy.codetest.model.User;
import com.sammy.codetest.service.ApiService;
import com.sammy.codetest.service.DistanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CodeTestController {

    private static final Logger log = LoggerFactory.getLogger(CodeTestController.class);

    private final DistanceService distanceService;
    private final ApiService apiService;

    @Autowired
    public CodeTestController(DistanceService distanceService, ApiService apiService) {
        this.distanceService = distanceService;
        this.apiService = apiService;
    }

    @GetMapping("users/city/{cityName}")
    public List<User> getCityUsers(@PathVariable String cityName) {
        log.info(String.format("Calling external API for all users listed as living in %s", cityName));
        return apiService.getUsersOfCity(cityName);
    }

    @GetMapping("users/city/{cityName}/nearby")
    public List<User> getNearbyCityUsers(@PathVariable String cityName) {
        log.info(String.format("Calling external API for all users living within 50 miles of %s", cityName));
        return apiService.getUsers().stream()
                .filter(user -> distanceService.distanceToCity(user.getLatitude(), user.getLongitude(), cityName) < 50)
                .collect(Collectors.toList());
    }

    @RestControllerAdvice
    public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(value = CityNotFoundException.class)
        protected ResponseEntity<Object> handleUnknownCity(CityNotFoundException ex, WebRequest request) {
            log.error(String.format("%s is not a recognised city", ex.getCityName()));
            return handleExceptionInternal(
                    ex,
                    new ErrorResponse(ex.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

}
