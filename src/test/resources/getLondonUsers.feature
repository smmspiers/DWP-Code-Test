Feature: Retrieving users listed as living in a City

  Scenario: Retrieving users listed as living in London
    When the API receives a GET request for users listed as living in "London"
    Then the API should return all the users in "output1"
    And the response should be in JSON format
    And the response status should be 200

  Scenario: Retrieving users listed as living in an unknown city
    When the API receives a GET request for users listed as living in "Berlin"
    Then the API should return the error response for "Berlin"
    And the response should be in JSON format
    And the response status should be 404
