package com.sammy.codetest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sammy.codetest.config.CodeTestConfigurationProperties;
import com.sammy.codetest.exception.ErrorResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefs {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefs.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CodeTestConfigurationProperties config;
    @LocalServerPort
    private int port;
    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String actualResponseBody;
    private HttpStatus actualHttpStatus;
    private HttpHeaders actualHttpHeaders;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        // To deserialize LocalDateTime object in ErrorResponse
        objectMapper.registerModule(new JavaTimeModule());
    }

    @When("the API receives a GET request for users listed as living in {string}")
    public void theAPIReceivesAGETRequestForAllTheUsersListedAsLivingInLondon(String cityName)
            throws IOException, URISyntaxException {
        String mockResponse = getJson(String.format("src/test/resources/mock_api_responses/%s.json", cityName));
        String mockUri = String.format("/city/%s/users", cityName);
        mockExternalApi(mockUri, mockResponse);
        String uri = "/users/city/" + cityName;
        callCodeTestApi(uri);
    }

    @When("the API receives a GET request for users 50 miles from {string}")
    public void theAPIReceivesAGETRequestForAllTheUsers50MilesFromLondon(String cityName)
            throws IOException, URISyntaxException {
        String mockResponse = getJson("src/test/resources/mock_api_responses/Users.json");
        String mockUri = "/users";
        mockExternalApi(mockUri, mockResponse);
        String uri = String.format("/users/city/%s/nearby", cityName);
        callCodeTestApi(uri);
    }

    @Then("the API should return all the users in {string}")
    public void theAPIShouldReturnAllTheUsersIn(String fileName) throws IOException {
        String expectedJson = getJson(String.format("src/test/resources/expected_responses/%s.json", fileName));
        assertEquals(expectedJson, actualResponseBody);
    }

    @Then("the API should return the error response for {string}")
    public void theAPIShouldReturnTheErrorResponseIn(String cityName) throws JsonProcessingException {
        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        String actualErrorMessage = actualErrorResponse.getError();
        String expectedErrorMessage = String.format("Unknown city %s. Please enter a city that the API recognises.", cityName);
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

    @And("the response should be in JSON format")
    public void theResponseShouldBeInTheCorrectFormat() {
        MediaType actualMediaType = actualHttpHeaders.getContentType();
        MediaType expectedMediaType = new MediaType("application", "json");
        assertEquals(expectedMediaType, actualMediaType);
    }

    @And("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        HttpStatus expectedStatus = HttpStatus.valueOf(statusCode);
        assertEquals(expectedStatus, actualHttpStatus);
    }

    private void callCodeTestApi(String uri) throws JsonProcessingException {
        try {
            ResponseEntity<Object> response = new RestTemplate().exchange(
                    createURLWithPort(uri),
                    HttpMethod.GET,
                    null,
                    Object.class);
            actualResponseBody = objectMapper.writeValueAsString(response.getBody());
            actualHttpStatus = response.getStatusCode();
            actualHttpHeaders = response.getHeaders();
        } catch (HttpClientErrorException e) {
            actualResponseBody = e.getResponseBodyAsString();
            actualHttpStatus = e.getStatusCode();
            actualHttpHeaders = e.getResponseHeaders();
        }
    }

    private void mockExternalApi(String uri, String response) throws URISyntaxException {
        LOGGER.info("Initialising mock for external API");
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(config.getCityApiUrl() + uri)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));
    }

    private String getJson(String filePath) throws IOException {
        String json = Files.readString(Paths.get(filePath), Charset.defaultCharset());
        // Reducing pretty json to compact form
        return objectMapper.readValue(json, JsonNode.class).toString();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
