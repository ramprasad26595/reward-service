# Reward Service

Spring Boot 4.1 and Java 17 REST API for calculating retailer reward points.

## Reward Rule

A customer earns:

- 2 points for every whole dollar spent over `$100` in a transaction.
- 1 point for every whole dollar spent between `$50` and `$100`.
- Example: `$120` earns `2 x $20 + 1 x $50 = 90` points.

## API

- `GET /api/v1/customers` lists the seeded demo customers.
- `GET /api/v1/rewards?customerId=1&startDate=2026-04-01&endDate=2026-06-30` returns monthly and total rewards for a dynamic date range.
- `GET /h2-console` opens the in-memory H2 database console.

## Technical Details

- Java 17 release target.
- Spring Boot 4.1.0.
- Spring Web MVC, Spring Data JPA, H2, Bean Validation, Actuator.
- Startup dummy data is loaded through `CommandLineRunner`.
- JaCoCo is configured for test coverage reporting and build quality gates.

## Run

```bash
mvn spring-boot:run
```

## Test And Coverage

```bash
mvn test
mvn verify
```

The JaCoCo report is generated at `target/site/jacoco/index.html`.

## H2 Console
- `http://localhost:8080/h2-console`
- JDBC URL: jdbc:h2:mem:rewards
