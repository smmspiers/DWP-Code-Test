package com.sammy.codetest.exception;

public class CityNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Unknown city %s. Please enter a city that the API recognises.";

    private String cityName;

    public CityNotFoundException(String cityName) {
        super(String.format(MESSAGE, cityName));
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
