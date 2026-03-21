# musketeer-spring-boot-client

Spring Boot client for the Musketeer API, tested against a
Contracteer mock server using
[contracteer-mockserver-spring](https://github.com/sabai-tech/contracteer/tree/main/contracteer-mockserver-spring).
This project walks through the test setup, how the mock
server validates requests and determines responses, and
what each test exercises.

See the [Mock an API with Spring Boot](https://sabai-tech.github.io/contracteer/latest/getting-started/mockserver-spring/) guide for the full documentation.

## Prerequisites

- Java 21
- `musketeer-spec` published to Maven Local
  (see [musketeer-spec](../musketeer-spec/))

## Dependencies

```kotlin
// Mock server (Spring integration)
testImplementation("tech.sabai.contracteer:contracteer-mockserver-spring:<version>")

// OpenAPI specification
implementation("tech.sabai.contracteer.examples:musketeer-spec:1.0.0")
```

## Mock Server Setup

`@ContracteerMockServer` starts a mock server from the
OpenAPI specification, injects its base URL into a Spring
property, and stops it when the test context closes.

```java
@SpringBootTest
@ActiveProfiles("test")
@ContracteerMockServer(
        openApiDoc = "classpath:musketeer-api.yaml",
        baseUrlProperty = "musketeer.api.base-url"
)
class MusketeerApiClientSpringBootTest {

  @Autowired
  MusketeerApiClient client;

  // tests go here
}
```

- `baseUrlProperty` injects the mock server's base URL
into the Spring property `musketeer.api.base-url`. The
`MusketeerApiClient` reads this property via
`@Value("${musketeer.api.base-url}")`, so it automatically
points to the mock server.

- `@ActiveProfiles("test")` disables the CLI runner that
would otherwise start on boot.

- `openApiDoc` accepts a file path, an HTTP(S) URL, or a
classpath resource (e.g. `classpath:openapi.yaml`). Here
the spec comes from the `musketeer-spec` dependency on
the classpath.

## How the Mock Server Responds

The mock server is not a hand-written stub. For each
incoming request, it validates against the full OpenAPI
schema and determines the response from the specification.

### Request Validation

Sending a request body with rank `KNIGHT` to
`POST /musketeers` triggers a 400 response. The
specification declares an enum constraint on the `rank`
field:

```yaml
rank:
  type: string
  enum:
    - CADET
    - MUSKETEER
    - CAPTAIN
```

The mock server validates the request body, finds that
`KNIGHT` is not in the enum, and returns 400 because the
operation defines a 400 response. No one wrote a mock
rule for this -- the schema itself drives the rejection.

### Scenario Matching

Sending `{name: "d'Artagnan", rank: "CADET",
weapon: "Rapier"}` to `POST /musketeers` matches the
`D_ARTAGNAN_JOINS` scenario defined in the specification:

```yaml
post:
  requestBody:
    content:
      application/json:
        examples:
          D_ARTAGNAN_JOINS:
            value:
              name: d'Artagnan
              rank: CADET
              weapon: Rapier
  responses:
    '201':
      headers:
        Location:
          examples:
            D_ARTAGNAN_JOINS:
              value: /musketeers/4
```

The mock server matches the request values to the
`D_ARTAGNAN_JOINS` scenario and returns 201 with
`Location: /musketeers/4`.

### Status-Code-Prefixed Scenarios

Calling `GET /musketeers/999` sends a valid request
(999 is an integer), and the value matches the
`404_UNKNOWN_MUSKETEER` scenario:

```yaml
examples:
  404_UNKNOWN_MUSKETEER:
    value: 999
```

The mock server returns 404. The key's prefix (`404_`)
targets the 404 response directly.

### Schema-Only Response

Calling `GET /musketeers` hits an operation with no
`examples` in the specification. No scenario to match.
The mock server generates a response with random values
conforming to the response schema (an array of
`Musketeer` objects).

### The 418 Diagnostic

If the mock server receives a request that matches an
operation but cannot determine the correct response --
for example, the request values partially match multiple
scenarios -- it returns 418. The 418 body contains the
nearest matching scenarios and why each did not fully
match. This is a debugging tool, not an error code from
your API.

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

## Running the CLI

The project includes a `MusketeerCli` command-line runner
for manual testing against a real server. Start the server
(see [musketeer-spring-boot-server](../musketeer-spring-boot-server/))
and run:

```bash
./gradlew bootRun --args="list-musketeers"
./gradlew bootRun --args="get-musketeer 1"
./gradlew bootRun --args="enlist-musketeer d'Artagnan CADET Rapier"
```

The base URL defaults to `http://localhost:8080` and is
configurable via `musketeer.api.base-url` in
`application.yml`.
