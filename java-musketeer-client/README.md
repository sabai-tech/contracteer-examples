# java-musketeer-client

Demonstrates how to run Contracteer's `MockServer`
programmatically with JUnit 5. No Spring Boot, no
annotations -- just start the server, run your tests,
and stop it.

The production client (`MusketeerApiClient`) is
intentionally minimal: `java.net.http.HttpClient` with
Jackson. The focus is on the mock server integration, not
the client itself.

## Prerequisites

- Java 21
- `musketeer-spec` published to Maven Local
  (see [musketeer-spec](../musketeer-spec/))

## Dependencies

```kotlin
// Mock server (test dependency)
testImplementation("tech.sabai.contracteer:contracteer-mockserver:<version>")

// OpenAPI specification (on the classpath)
implementation("tech.sabai.contracteer.examples:musketeer-spec:1.0.0")
```

## The Client

`MusketeerApiClient` wraps `java.net.http.HttpClient` with
Jackson for JSON serialization. The constructor takes a base
URL -- no framework, no dependency injection:

```java
var client = new MusketeerApiClient("http://localhost:8080");
```

Seven operations:

| Method                                              | HTTP                              | Description                                                   |
|-----------------------------------------------------|-----------------------------------|---------------------------------------------------------------|
| `listMusketeers()`                                  | `GET /musketeers`                 | Returns all musketeers                                        |
| `getMusketeer(int id)`                              | `GET /musketeers/{id}`            | Returns `Optional`, empty on 404                              |
| `enlistMusketeer(CreateMusketeer)`                  | `POST /musketeers`                | Follows the `Location` header to return the created musketeer |
| `listMissions()`                                    | `GET /missions`                   | Returns all missions                                          |
| `getMission(int id)`                                | `GET /missions/{id}`              | Returns `Optional`, empty on 404                              |
| `createMission(CreateMission)`                      | `POST /missions`                  | Follows the `Location` header to return the created mission   |
| `getMusketeerMissions(int musketeerId, String status)` | `GET /musketeers/{id}/missions` | Returns missions filtered by status                           |

## Mock Server Setup

Load the OpenAPI specification, create a `MockServer`, and
start it:

```java
var result = OpenApiLoader.loadOperations("classpath:musketeer-api.yaml");
if (result.isFailure())
  throw new IllegalStateException("Failed to load spec: " + result.errors());

mockServer = new MockServer(requireNonNull(result.getValue()));
mockServer.start();
```

`OpenApiLoader.loadOperations` accepts a file path, an
HTTP(S) URL, or a classpath resource. `MockServer` starts
on a random port by default. After `start()`, call
`mockServer.port()` to get the actual port.

This pattern works with any test framework -- the mock
server is just an HTTP server you control programmatically.

## JUnit 5 Lifecycle

```java
@BeforeAll
static void startMockServer() {
    var result = OpenApiLoader.loadOperations("classpath:musketeer-api.yaml");
    if (result.isFailure())
      throw new IllegalStateException("Failed to load spec: " + result.errors());
    mockServer = new MockServer(requireNonNull(result.getValue()));
    mockServer.start();
}

@AfterAll
static void stopMockServer() {
    mockServer.stop();
}

@BeforeEach
void setUp() {
    client = new MusketeerApiClient("http://localhost:" + mockServer.port());
}
```

`@BeforeAll` / `@AfterAll` manage the mock server
lifecycle. `@BeforeEach` creates a fresh client pointing at
the mock server's port.

## How the Mock Server Responds

The mock server validates every incoming request against
the OpenAPI schema and determines its response from the
specification:

- **Valid requests** receive a spec-compliant response.
  When request values match a scenario defined via OpenAPI
  `examples`, the mock server returns that scenario's
  response. Otherwise, it generates random values from
  the schema.
- **Invalid requests** (e.g. an enum value `KNIGHT` that
  is not in the schema) are rejected with 400 when the
  operation defines a 400 response.
- **Ambiguous requests** that match multiple scenarios or
  cannot be resolved receive a 418 diagnostic response
  explaining why.

See the
[Spring Boot client example](../musketeer-spring-boot-client/)
for a detailed walkthrough with OpenAPI spec excerpts
showing how scenario matching, request validation, and
schema-only responses work.

## Writing Assertions

Tests assert response structure (not null, present, positive
id) rather than specific values. Even when a scenario
matches and returns deterministic values, tests should not
depend on them -- the exact values may change when the
specification evolves. Coupling assertions to example data
would make tests brittle and turn them into functional
tests rather than client integration tests.

## Running the Tests

```bash
./gradlew test
```
