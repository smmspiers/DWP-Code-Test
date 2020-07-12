# Rest API project 
This is a Java Spring-boot application that uses an external API to return either all users who are 
listed as living in some city or 50 miles (ca. 80 km) from some city.  
The external API's URL: https://bpdts-test-app.herokuapp.com.

## How to run
The Easiest way to run the application would be to clone this repo and open it up in your favourite IDE.
Otherwise, navigate to the root directory of the project and run
```shell script
mvn spring-boot:run
``` 

## Adding more cities
Currently, the only city that can be selected is London. One can easily add more cities, together 
with their latitudes and longitudes, to the application.yml file. However, this will only work for
the ```/users/city/{cityName}/nearby``` endpoint.

## Documentation
As well as the following documentation on the API, I have configured the SwaggerUI for this API which 
can be viewed at ```/swagger-ui.html```; you may find this useful.

### Get list of users listed as living in a city
- Endpoint:
```GET /users/city/{cityName}```
- Example request:
```curl --request GET -sL --url 'http://localhost:8080/users/city/London'```
- Success Response:
    - Code: 200 OK
    - Example content:
    ```json
    [
      {
        "id": 1,
        "first_name": "Cosmo",
        "last_name": "Kindleside",
        "email": "ckindleside1b@cbsnews.com",
        "ip_address": "19.127.125.220",
        "latitude": 36.195409,
        "longitude": 113.116404
      },
      {
        "id": 2,
        "first_name": "Nikolai",
        "last_name": "McGarrie",
        "email": "nmcgarrie1c@techcrunch.com",
        "ip_address": "141.121.121.91",
        "latitude": 57.6814887,
        "longitude": 39.8556491
      }
    ]
    ```
- Error Response:
    - Code: 404 NOT FOUND
    - Example content:
    ```json
        {
            "timestamp": "2020-07-12T19:37:21.836713",
            "error": "Unknown city paris. Please enter a city that the API recognises.",
            "status": "NOT_FOUND",
            "statusCode": 404
        }
    ```

### Get list of users living 50 miles (ca. 80 km) from a city
- Endpoint:
```GET /users/city/{cityName}/nearby```
- Example request:
```curl --request GET -sL --url 'http://localhost:8080/users/city/London/nearby'```
- Success Response
    - Code: 200 OK
    - Example content:
    ```json
    [
      {
        "id": 1,
        "first_name": "Augusta",
        "last_name": "McGaw",
        "email": "amcgaw6d@hexun.com",
        "ip_address": "38.145.17.155",
        "latitude": 51.613346,
        "longitude": -0.366794
      },
      {
        "id": 3,
        "first_name": "Mick",
        "last_name": "D'Agostini",
        "email": "mdagostini6f@opensource.org",
        "ip_address": "152.145.195.4",
        "latitude": 51.337408,
        "longitude": 0.017070
      }
    ]
    ```
- Error Response
    - Code: 404 NOT FOUND
    - Example content:
    ```json
        {
            "timestamp": "2020-07-12T19:37:21.836713",
            "error": "Unknown city paris. Please enter a city that the API recognises.",
            "status": "NOT_FOUND",
            "statusCode": 404
        }
    ```

###### Note: This was part of a coding test I did for the DWP.