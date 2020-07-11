Feature: Retrieving users living 50 miles from a City

  Scenario: Retrieving users living 50 miles from London
    When the API receives a GET request for users 50 miles from "London"
    Then the API should return all the users in "output2"
    And the response should be in JSON format
    And the response status should be 200

  Scenario: Retrieving users living 50 miles from an unknown city
    When the API receives a GET request for users 50 miles from "Berlin"
    Then the API should return the error response for "Berlin"
    And the response should be in JSON format
    And the response status should be 404
