# Reward Service

Spring Boot 4.1 and Java 17 REST API for calculating retailer reward points.

## Reward Rule

A customer earns:

- 2 points for every whole dollar spent over `$100` in a transaction.
- 1 point for every whole dollar spent between `$50` and `$100`.
- Example: `$120` earns `2 x $20 + 1 x $50 = 90` points.

## Base URL

All API endpoints are served from:

`http://localhost:8080`

## Endpoints

### 1) Get customers

`GET /api/v1/customers`

Returns the seeded customer list, sorted by full name.

Request:

```http
GET /api/v1/customers
```

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

Returns the reward summary for one customer over a dynamic date range.

Request query parameters:

- `customerId` - required
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
      "month": "2026-04",
      "transactionCount": 2,
      "totalSpend": 204.25,
      "points": 124,
      "transactions": [
        {
          "transactionId": 1,
          "transactionDate": "2026-04-03",
          "amount": 84.25,
          "merchantName": "Reliance Fresh",
          "points": 34,
          "breakdown": {
            "dollarsBetweenFiftyAndOneHundred": 34,
            "dollarsOverOneHundred": 0,
            "pointsBetweenFiftyAndOneHundred": 34,
            "pointsOverOneHundred": 0
          }
        }
      ]
    }
  ],
  "generatedAt": "2026-06-17T07:45:00Z"
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
- `monthlyRewards`: monthly breakdown
- `generatedAt`: response generation timestamp

Monthly reward fields:

- `month`: month in `yyyy-MM` format
- `transactionCount`: transactions in that month
- `totalSpend`: total spending for the month
- `points`: reward points earned in the month
- `transactions`: per-transaction reward details

Transaction reward fields:

- `transactionId`: transaction identifier
- `transactionDate`: transaction date
- `amount`: purchase amount
- `merchantName`: merchant name
- `points`: reward points for the transaction
- `breakdown`: point calculation details

Breakdown fields:

- `dollarsBetweenFiftyAndOneHundred`: dollars counted at 1 point each
- `dollarsOverOneHundred`: dollars counted at 2 points each
- `pointsBetweenFiftyAndOneHundred`: points earned from the $50-$100 portion
- `pointsOverOneHundred`: points earned from the over-$100 portion

Error response:

```json
{
  "timestamp": "2026-06-17T07:45:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Customer 99 was not found",
  "path": "/api/v1/rewards",
  "details": []
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

- Java 17 release target.
- Spring Boot 4.1.0.
- Spring Web MVC, Spring Data JPA, H2, Bean Validation, Actuator.
- JaCoCo is configured for test coverage reporting and build quality gates.
- Tests use Mockito-based unit testing.

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
