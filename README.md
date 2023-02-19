## Delivery Fee Calculator
- [Delivery Fee Calculator](#delivery-fee-calculator)
  * [Description](#description)
  * [Building and running project](#building-and-running-project)
  * [Running project with Docker](#running-project-with-docker)
  * [Testing](#testing)

### Description
This project is my solution for Wolt Summer 2023 Engineering Internship assignment.

Source: https://github.com/woltapp/engineering-summer-intern-2023

### Building and running project
Java 11 should be used for project to run.

After opening project in IntelliJ add new "Run configuration" and choose "Play 2".
Now application can be run and tested on port **9000**.

There are also unit tests that cover validation, delivery fee calculation and simple functional tests for the controller / API.

### Running project with Docker

You can also run this project by using this docker image:

Load image:
```bash

docker pull daniilkurachkin/wolt-daniil-kurachkin:v1.0   
```
Run container:
```bash

docker run -d --publish 9000:9000 --name daniil-kurachkin daniilkurachkin/wolt-daniil-kurachkin:v1.0
```

### Testing

Endpoint for delivery calculation is /calculateDeliveryFee (POST).


Example of the curl command to test API:

```bash
curl -X POST -H "Content-Type: application/json" -d \
     '{"cart_value": 790, "delivery_distance": 2235, "number_of_items": 4, "time": "2021-10-12T13:00:00Z"}' \
     http://localhost:9000/calculateDeliveryFee
```


API expects input in JSON format with following structure:
```json

{
    "cart_value": 790, 
    "delivery_distance": 2235, 
    "number_of_items": 4,
    "time": "2021-10-12T13:00:00Z"
}
```

If data is valid service will respond with:
```json

{
    "delivery_fee": 710
}
```

If input has invalid *scheme* service will respond with:
```json

{
    "title": "Invalid payload",
    "detail": "DeliveryInformation can't be created from request body",
    "code": 400
}
```

If input has invalid *values* service will respond with:
```json

{
    "title": "Validation failed",
    "detail": "Invalid number of items! At least one item must be delivered",
    "code": 400
}
```
