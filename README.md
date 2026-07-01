# Reward Service

Spring Boot 3.5.0 and Java 17 REST API for calculating retailer reward points.

## Reward Rule

A customer earns:

- 2 points for every whole dollar spent over `$100` in a transaction
- 1 point for every whole dollar spent between `$50` and `$100`
- Example: `$120` earns `90` points

### Fractional Amounts

Reward points are calculated on **whole dollars only** — any cents are dropped
(rounded down) before points are computed. This is applied per transaction.

Worked examples:

- `$84.25` → whole dollars are `$84`, which is `$34` over `$50` → `34` points
- `$100.99` → whole dollars are `$100` → `50` points (the cents above `$100` are ignored)
- `$120.00` → `50` points for the `$50`–`$100` band plus `2 × 20` for the amount
  over `$100` → `90` points

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
  "customerDetails": {
    "customerId": 1,
    "fullName": "Aarav Sharma",
    "email": "aarav.sharma@example.com"
  },
  "startDate": "2026-04-01",
  "endDate": "2026-06-30",
  "transactionCount": 5,
  "transactionDetails": [
    {
      "transactionId": 1,
      "transactionDate": "2026-04-03",
      "merchantName": "Reliance Fresh",
      "amount": 84.25,
      "points": 34
    },
    {
      "transactionId": 2,
      "transactionDate": "2026-04-21",
      "merchantName": "Westside",
      "amount": 120.00,
      "points": 90
    },
    {
      "transactionId": 3,
      "transactionDate": "2026-05-09",
      "merchantName": "Cafe Coffee Day",
      "amount": 52.40,
      "points": 2
    },
    {
      "transactionId": 4,
      "transactionDate": "2026-05-26",
      "merchantName": "Pepperfry",
      "amount": 211.75,
      "points": 272
    },
    {
      "transactionId": 5,
      "transactionDate": "2026-06-12",
      "merchantName": "Croma",
      "amount": 149.99,
      "points": 148
    }
  ],
  "monthlyRewardPoints": [
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
  ],
  "totalRewardPoints": 546
}
```

The per-transaction `points` make every reward calculation verifiable directly
from the response, without reading the source. For example `$84.25` ignores the
cents and scores `34` points (`$84 − $50`); each month's `points` is the sum of
its transactions, and `totalRewardPoints` is the sum across all months.

Response fields:

- `customerDetails`: customer identity block (`customerId`, `fullName`, `email`)
- `startDate`: request start date
- `endDate`: request end date
- `transactionCount`: total transactions found in the date range
- `transactionDetails`: per-transaction breakdown with the points each one earned
- `monthlyRewardPoints`: month-by-month summary (every month in the range, even
  empty ones)
- `totalRewardPoints`: total reward points across the full range

Transaction detail fields:

- `transactionId`: transaction identifier
- `transactionDate`: date the purchase was made
- `merchantName`: merchant name
- `amount`: purchase amount
- `points`: reward points earned for that single transaction

Monthly reward fields:

- `year`: calendar year
- `month`: month name
- `transactionCount`: transactions in that month
- `totalSpend`: total spending for the month
- `points`: reward points earned in the month

Error responses use the same payload shape. Validation problems — a missing
parameter, a non-numeric `customerId`, or a malformed/invalid date such as
`2026-13-45` — return `400 Bad Request`. An `endDate` earlier than `startDate`
also returns `400`. An unknown `customerId` returns `404 Not Found`.

```json
{
  "timestamp": "2026-06-23T07:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/rewards",
  "details": [
    "startDate: '2026-13-45' could not be converted to LocalDate"
  ]
}
```

## Seed Data

The application loads demo data from `src/main/resources/data.sql`, which Spring
runs automatically after Hibernate creates the schema
(`spring.jpa.defer-datasource-initialization=true`). The seed data can be changed
without recompiling the application.

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