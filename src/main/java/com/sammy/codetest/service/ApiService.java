package com.sammy.codetest.service;

import com.sammy.codetest.config.CodeTestConfigurationProperties;
import com.sammy.codetest.exception.CityNotFoundException;
import com.sammy.codetest.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    private final RestTemplate restTemplate;
    private final CodeTestConfigurationProperties config;

    @Autowired
    public ApiService(RestTemplate restTemplate, CodeTestConfigurationProperties config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public List<User> getUsers() {
        final Optional<List<User>> users = Optional.ofNullable(
                restTemplate.exchange(
                        config.getCityApiUrl() + "/users",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {}).getBody());
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println("SADasdfasdfasdf");
        System.out.println(users.get().toString());
        return users.get();
    }

    public List<User> getUsersOfCity(String cityName) {
        final Optional<List<User>> users = Optional.ofNullable(
                restTemplate.exchange(
                        String.format(config.getCityApiUrl() + "/city/%s/users", cityName),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {}).getBody());
        if (users.isEmpty() || users.get().isEmpty()) {
            throw new CityNotFoundException(cityName);
        }
        System.out.println(users.get().toString());
        return users.get();
    }
}
