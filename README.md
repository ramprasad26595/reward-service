# Reward Service

Spring Boot 3.5.0 and Java 17 REST API for calculating retailer reward points.

## Reward Rule

A customer earns:

- 2 points for every whole dollar spent over `$100` in a transaction
- 1 point for every whole dollar spent between `$50` and `$100`
- Example: `$120` earns `90` points

## Base URL

All API endpoints are served from:

`http://localhost:8080`

## Endpoints

### 1) Get customers

`GET /api/v1/customers`

Returns the seeded customer list, sorted by full name.

Response `200 OK`:

```json
[
  {
    "customerId": 1,
    "fullName": "Aarav Sharma",
    "email": "aarav.sharma@example.com"
  }
]
```

Response fields:

- `customerId`: customer identifier
- `fullName`: customer full name
- `email`: customer email address

### 2) Calculate rewards

`GET /api/v1/rewards`

Returns the reward summary for one customer over a date range.

Request query parameters:

- `customerId` - required, positive number
- `startDate` - required, format `yyyy-MM-dd`
- `endDate` - required, format `yyyy-MM-dd`

Example request:

```http
GET /api/v1/rewards?customerId=1&startDate=2026-04-01&endDate=2026-06-30
```

Response `200 OK`:

```json
{
  "customerId": 1,
  "customerName": "Aarav Sharma",
  "email": "aarav.sharma@example.com",
  "startDate": "2026-04-01",
  "endDate": "2026-06-30",
  "transactionCount": 5,
  "totalPoints": 546,
  "monthlyRewards": [
    {
      "year": 2026,
      "month": "April",
      "transactionCount": 2,
      "totalSpend": 204.25,
      "points": 124
    },
    {
      "year": 2026,
      "month": "May",
      "transactionCount": 2,
      "totalSpend": 264.15,
      "points": 274
    },
    {
      "year": 2026,
      "month": "June",
      "transactionCount": 1,
      "totalSpend": 149.99,
      "points": 148
    }
  ]
}
```

Response fields:

- `customerId`: customer identifier
- `customerName`: customer full name
- `email`: customer email address
- `startDate`: request start date
- `endDate`: request end date
- `transactionCount`: total transactions found in the date range
- `totalPoints`: total reward points across the full range
- `monthlyRewards`: month-by-month summary

Monthly reward fields:

- `year`: calendar year
- `month`: month name
- `transactionCount`: transactions in that month
- `totalSpend`: total spending for the month
- `points`: reward points earned in the month

Error response:

```json
{
  "timestamp": "2026-06-23T07:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/rewards",
  "details": [
    "endDate: must not be null"
  ]
}
```

## Seed Data

The application loads dummy data on startup through `CommandLineRunner`.

Seeded customers:

- Aarav Sharma
- Priya Iyer
- Rohan Mehta

Sample merchants include:

- Reliance Fresh
- Westside
- Cafe Coffee Day
- Pepperfry
- Croma
- Apollo Pharmacy
- Decathlon
- Bata
- Tanishq
- Crossword
- Big Bazaar
- MakeMyTrip

## Technical Details

- Java 17 release target
- Spring Boot 3.5.0
- Spring Web MVC, Spring Data JPA, H2, Bean Validation, Actuator
- Lombok is used to reduce boilerplate in the domain and service layers
- JaCoCo is configured for test coverage reporting and build quality gates
- Tests use Mockito-based unit tests plus Spring integration tests against H2

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
- JDBC URL: `jdbc:h2:mem:rewards`
- Username: `sa`
- Password: empty
