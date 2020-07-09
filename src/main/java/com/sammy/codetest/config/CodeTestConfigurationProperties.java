package com.sammy.codetest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "codetest")
public class CodeTestConfigurationProperties {

    private List<City> cities;
    private String cityApiUrl;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public String getCityApiUrl() {
        return cityApiUrl;
    }

    public void setCityApiUrl(String cityApiUrl) {
        this.cityApiUrl = cityApiUrl;
    }
}
