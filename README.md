# Database Connections and Advanced Queries API

A Spring Boot REST API demonstrating complex relational data modeling (One-to-Many, Many-to-Many), advanced JPA querying techniques (derived, JPQL, native, Specification API), transaction management, and N+1 query detection/resolution.

## Tech Stack
- Java 21
- Spring Boot 3.2.4
- Spring Data JPA
- MySQL (Docker) / H2 (tests)
- Lombok
- Spring Validation
- springdoc-openapi (Swagger UI)

## Domain Model
- **Category** ↔ **Book**: Many-to-Many relationship (a book can belong to multiple categories, a category can have multiple books)
- **Order** → **OrderItem**: One-to-Many relationship (an order contains multiple order items)
- **OrderItem** → **Book**: Many-to-One relationship (each order item references one book)

## Prerequisites
- Java 21
- Maven
- Docker (for MySQL) or a local MySQL instance

## Installation & Setup

1. **Clone the repository**
```bash
   git clone <repository-url>
   cd db-queries-api
```

2. **Start MySQL via Docker**
```bash
   docker run --name db-queries-mysql -e MYSQL_ROOT_PASSWORD=root1234 -e MYSQL_DATABASE=db_queries_db -p 3307:3306 -d mysql:8.0
```

3. **Set Environment Variables**
   Configure the following environment variables (or refer to `.env.example`):
   - `DB_URL`: `jdbc:mysql://localhost:3307/db_queries_db`
   - `DB_USERNAME`: `root`
   - `DB_PASSWORD`: `root1234`

4. **Run the Application**
```bash
   ./mvnw spring-boot:run
```

5. **Run Tests** (uses an in-memory H2 database, no MySQL required)
```bash
   ./mvnw test
```

## API Endpoints

| Method | Path | Description | Example Request Body |
|---|---|---|---|
| POST | `/api/categories` | Create a category | `{"name":"Fiction"}` |
| GET | `/api/categories` | List all categories | — |
| POST | `/api/books` | Create a book with categories | `{"title":"Dune","author":"Frank Herbert","price":25.99,"publishedDate":"1965-08-01","categoryIds":[1]}` |
| GET | `/api/books/search` | Dynamic search (optional query params: `title`, `author`, `minPrice`, `maxPrice`, `category`) | — |
| POST | `/api/orders` | Place an order (transactional, writes Order + OrderItems) | `{"customerName":"Ali Aliyev","items":[{"bookId":1,"quantity":2}]}` |
| GET | `/api/orders?status=PENDING` | Get orders by status (uses JOIN FETCH to avoid N+1) | — |

## API Documentation (Swagger UI)

Once the application is running:
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Key Implementation Notes

### Relationship Design
`Book` owns the `@ManyToMany` relationship to `Category` via a `book_category` join table. `Order` owns a `@OneToMany` relationship to `OrderItem` with `cascade = CascadeType.ALL` and `orphanRemoval = true`. Both relationships use manual `equals()`/`hashCode()` (based on ID only) instead of Lombok's `@Data`/`@EqualsAndHashCode` to avoid infinite recursion and lazy-loading issues on bidirectional associations. Entities are never returned directly from controllers — dedicated response DTOs (`BookResponse`, `CategoryResponse`, `OrderResponse`) prevent circular JSON serialization.

### Querying Styles
`BookRepository` demonstrates three querying approaches: a derived query method for simple filters, a JPQL `@Query` for readable object-oriented queries, and a native SQL query for aggregations (average price per category) that are cleaner to express in raw SQL.

### Dynamic Search
`GET /api/books/search` uses JPA's `Specification` API (`BookSpecification`) to build null-safe, composable filters (title, author, price range, category) combined at runtime based on which query parameters are present.

### Transaction Management
`OrderService.placeOrder()` is annotated with `@Transactional`. If any book in the request doesn't exist, a `BookNotFoundException` is thrown before the order is saved, and the entire operation — including any order items already processed in the loop — is rolled back. This is verified by an integration test (`OrderServiceTest`) using an in-memory H2 database.

### N+1 Query Resolution
`OrderRepository` provides two methods for fetching orders by status: a naive derived query (`findByStatus`) that triggers a separate SQL query for each order's items when accessed, and a fixed version (`findByStatusWithItems`) using JPQL `LEFT JOIN FETCH` on both `items` and `items.book`, retrieving everything in a single SQL query. The `GET /api/orders` endpoint uses the fixed version.

## Testing
Run `./mvnw test` to execute `OrderServiceTest`, which verifies that placing an order with an invalid book ID rolls back the transaction completely — no order or order items are persisted.
