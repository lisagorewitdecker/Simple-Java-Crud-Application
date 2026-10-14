# Simple Java Crud Application - Spring Boot Product Service with Login

A RESTful web service
built with Spring Boot,
Spring Data JPA, and
Spring Modulith for
managing product
inventory with
pagination, sorting,
filtering, and
validation.

---

## Overview

This project provides a
product management API
designed with clean
architecture principles:

- **Product Management**:
  CRUD endpoints for
  creating, retrieving,
  updating, and deleting
  products.
- **Query & Filtering**:
  Pagination, sorting,
  price range filtering,
  and keyword search.
- **Validation &
  Exception Handling**:
  Input validation for
  product models and
  centralized error
  response mapping.
- **In-Memory Database**:
  Embedded H2 database
  configured with
  HikariCP connection
  pooling for development
  and testing.
- **Modular Architecture**:
  Spring Modulith
  dependencies support the
  application's modular
  structure.
- **Monitoring &
  Metrics**: Spring Boot
  Actuator integration
  for health checks and
  observability.

---

## Tech Stack & Requirements

### Tech Stack

- **Language**: Java 17
- **Framework**: Spring
  Boot 4.1.1
    - Spring Web (REST
      API)
    - Spring Data JPA /
      Hibernate
    - Spring Boot
      Validation
      (Hibernate
      Validator)
    - Spring Boot
      Actuator
    - Spring Modulith
      2.1.1
- **Database**: H2
  Database (In-memory)
- **Utilities**: Project
  Lombok
- **Build Tool / Package
  Manager**: Apache Maven
  (via Maven Wrapper
  `mvnw` / `mvnw.cmd`)

### Requirements

- **JDK / JRE**: Java
  Development Kit (JDK)
  17 or higher
- **Maven**: Optional
  (bundled Maven Wrapper
  `./mvnw` is included)

---

## Entry Points

- **Main Application**:
  `com.example.demo.DemoApplication`
  (`src/main/java/com/example/demo/DemoApplication.java`)
- **Web API Base URL**: `http://localhost:8080/api/products`
- **Actuator Base URL**: `http://localhost:8080/actuator`
- **HTTP Client
  Requests**:
  requests.http` (sample
  requests for testing
  endpoints in IDE)

---

## Setup & Run

### 1. Clone & Navigate to Repository

```bash
git clone <repository-url>
cd simple-java-crud-application-login-products
```

### 2. Build the Application

On macOS/Linux:

```bash
./mvnw clean package
```

On Windows:

```cmd
mvnw.cmd clean package
```

### 3. Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or running the packaged
JAR:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The service will start on
port `8080` by default.

---

## Scripts & Commands

| Command                          | Description                                              |
|:---------------------------------|:---------------------------------------------------------|
| `./mvnw compile`                 | Compiles the source code                                 |
| `./mvnw test`                    | Runs unit and integration test suites                    |
| `./mvnw package`                 | Packages the application into a JAR in `target/`         |
| `./mvnw spring-boot:run`         | Runs the Spring Boot application locally                 |
| `./mvnw clean`                   | Cleans up the `target` build directory                   |
| `./mvnw spring-boot:build-image` | Builds an OCI container image via Spring Boot buildpacks |

---

## Environment Variables & Configuration

The application is
configured in
`src/main/resources/application.properties`.
You can override
properties via standard
Spring Boot mechanisms
(environment variables,
system properties, or
config profiles).

| Property                                     | Default Value                                                    | Description                                 |
|:---------------------------------------------|:-----------------------------------------------------------------|:--------------------------------------------|
| `server.port`                                | `8080`                                                           | Port for the HTTP server                    |
| `spring.datasource.url`                      | `jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` | JDBC URL for H2 in-memory database          |
| `spring.datasource.driverClassName`          | `org.h2.Driver`                                                  | Database JDBC driver                        |
| `spring.datasource.username`                 | `root`                                                           | Database user                               |
| `spring.datasource.password`                 | *(empty)*                                                        | Database password                           |
| `spring.datasource.hikari.minimum-idle`      | `5`                                                              | Minimum idle connections in HikariCP pool   |
| `spring.datasource.hikari.maximum-pool-size` | `10`                                                             | Maximum connections in HikariCP pool        |
| `spring.jpa.hibernate.ddl-auto`              | `update`                                                         | Hibernate schema management strategy        |
| `spring.jpa.open-in-view`                    | `false`                                                          | Disables Open EntityManager in View pattern |
| `spring.jpa.show-sql`                        | `false`                                                          | Disables SQL logging in stdout              |

> **Note**: MySQL and
> PostgreSQL drivers are
> managed in dependency
> management (`pom.xml`)
> and can be enabled for
> production databases by
> adding them as
> dependencies and
> overriding
> `spring.datasource.*`
> environment variables.

---

## API Endpoints

| Method   | Endpoint             | Description                                                                 |
|:---------|:---------------------|:----------------------------------------------------------------------------|
| `POST`   | `/api/products`      | Create a new product                                                        |
| `GET`    | `/api/products`      | Retrieve all products (supports pagination, sorting, search, price filters) |
| `GET`    | `/api/products/{id}` | Get product by ID                                                           |
| `PUT`    | `/api/products/{id}` | Update product by ID                                                        |
| `DELETE` | `/api/products/{id}` | Delete product by ID                                                        |
| `GET`    | `/actuator/health`   | Application health check endpoint                                           |

The sample request workflows
in `requests.http` require
authentication. Run the
login request first with
`admin` / `admin123` in
IntelliJ IDEA / WebStorm,
then run the API requests in
the same HTTP client session
so its login cookie is reused.

---

## Testing

Run all unit and
integration tests with:

```bash
./mvnw test
```

### Test Suite Includes:

- **
  `DemoApplicationTests`**:
  Context loading and
  Spring Modulith module
  verification test
  (`ApplicationModules.of(DemoApplication.class).verify()`).
- **
  `ProductControllerTest`**:
  Web tier tests
  verifying REST
  endpoints, pagination,
  validation handling,
  and responses.
- **
  `RmiServerManagerTest`**:
  Unit tests for RMI
  manager lifecycle and
  server handling.

---

## Project Structure

```
demo/
├── .mvn/                          # Maven wrapper binaries and configuration
├── inspections/                   # IDE / Qodana inspection rules
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── controller/        # REST Controllers (ProductController)
│   │   │   ├── dto/               # Data Transfer Objects (ProductRequest, ProductResponse)
│   │   │   ├── exception/         # Exception definitions & GlobalExceptionHandler
│   │   │   ├── model/             # JPA Entities (Product)
│   │   │   ├── repository/        # Spring Data JPA repositories (ProductRepository)
│   │   │   ├── service/           # Business logic services (ProductService)
│   │   │   ├── DemoApplication.java # Application entry point
│   │   │   └── RmiServerManager.java # RMI server helper / manager
│   │   └── resources/
│   │       └── application.properties # Application configuration
│   └── test/
│       └── java/com/example/demo/  # Integration & Unit test classes
├── HELP.md                        # Spring Boot starter reference documentation
├── mvnw                           # Maven Wrapper script for Unix/macOS
├── mvnw.cmd                       # Maven Wrapper script for Windows
├── pom.xml                        # Maven project descriptor & dependencies
├── qodana.yaml                    # Qodana code quality configuration
├── README.md                      # Project documentation
└── requests.http                  # HTTP client request collection
```

---

## License & Notes

- **License**: TODO: Add
  project license (e.g.
  MIT, Apache 2.0,
  Proprietary).
- **Authors**: TODO: Add
  repository maintainers
  and contact details.
