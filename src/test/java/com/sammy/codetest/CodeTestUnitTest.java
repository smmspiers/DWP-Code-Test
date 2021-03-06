package com.sammy.codetest;

import com.sammy.codetest.config.City;
import com.sammy.codetest.config.CodeTestConfigurationProperties;
import com.sammy.codetest.exception.CityNotFoundException;
import com.sammy.codetest.service.DistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodeTestUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeTestUnitTest.class);
    private static final List<City> CITIES = Arrays.asList(
            new City("Paris", 48.85341, 2.3488),
            new City("New York", 40.71427, -74.00597),
            new City("Rio de Janeiro", -22.90278, -43.2075));

    @Mock
    private CodeTestConfigurationProperties configMock;
    @InjectMocks
    private DistanceService distanceService;

    @BeforeEach
    public void initMocks() {
        LOGGER.info("Initialising mocks");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void distanceToCity_FromParisToNewYork_ShouldBeCorrect() {
        Mockito.when(configMock.getCities()).thenReturn(CITIES);
        final City paris = CITIES.get(0);
        final double expectedDistance = distanceService.distanceToCity(
                paris.getLatitude(),
                paris.getLongitude(),
                "New York");
        assertEquals(3627.0107003, expectedDistance, 0.001);
    }

    @Test
    public void distanceToCity_FromParisToRio_ShouldBeCorrect() {
        Mockito.when(configMock.getCities()).thenReturn(CITIES);
        final City paris = CITIES.get(0);
        final double expectedDistance = distanceService.distanceToCity(
                paris.getLatitude(),
                paris.getLongitude(),
                "Rio de Janeiro");
        assertEquals(5696.963509, expectedDistance, 0.001);
    }

    @Test
    public void distanceToCity_FromNewYorkToNewYork_ShouldBeZero() {
        Mockito.when(configMock.getCities()).thenReturn(CITIES);
        final City newYork = CITIES.get(1);
        final double expectedDistance = distanceService.distanceToCity(
                newYork.getLatitude(),
                newYork.getLongitude(),
                "New York");
        assertEquals(0, expectedDistance);
    }

    @Test
    public void distanceToCity_FromParisToBerlin_ShouldThrowException() {
        Mockito.when(configMock.getCities()).thenReturn(CITIES);
        final City paris = CITIES.get(0);
        final Exception exception = assertThrows(CityNotFoundException.class, () -> {
            distanceService.distanceToCity(paris.getLatitude(), paris.getLongitude(), "Berlin");
        });
        final String expectedMessage = "Unknown city Berlin. Please enter a city that the API recognises.";
        final String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}