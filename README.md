# Order Service

A small Java learning project that models an order management system with an in-memory store.

## Features

- Create, find, list, and cancel orders
- Order statuses: `CREATED`, `PAID`, `SHIPPED`, `CANCELLED`
- Stream-based queries: orders by status, total revenue, orders by customer, top customers
- Unit tests with JUnit 5

## Requirements

- Java 21
- Maven 3.9+

## Build

```
mvn clean package
```

## Run the tests

```
mvn test
```

## Run the app

```
java -jar target/order-service-1.0-SNAPSHOT.jar
```

## Project structure

```
src/main/java/com/example/orders/
  App.java                        demo entry point
  Order.java                      order with items, status, total
  OrderItem.java                  line item (record)
  OrderStatus.java                order status enum
  OrderService.java               business logic and queries
  OrderNotFoundException.java
  OrderNotCancellableException.java
src/test/java/com/example/orders/
  OrderServiceTest.java           unit tests
```