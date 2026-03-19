# musketeer-spec

OpenAPI specification for the Musketeer API, packaged as a
Maven artifact so all example projects can depend on it.

## What It Contains

A single file -- `musketeer-api.yaml` -- that defines a REST
API for managing musketeers and their missions:

| Operation | Description |
|-----------|-------------|
| `GET /musketeers` | List all musketeers |
| `POST /musketeers` | Enlist a new musketeer |
| `GET /musketeers/{id}` | Get a musketeer by ID |
| `GET /musketeers/{id}/missions` | Get missions assigned to a musketeer |
| `GET /missions` | List all missions |
| `POST /missions` | Create a new mission |
| `GET /missions/{id}` | Get a mission by ID |

The spec is packaged as a JAR and published to Maven Local.

## Publishing

```bash
./gradlew publishToMavenLocal
```

This publishes `tech.sabai.contracteer.examples:musketeer-spec:1.0.0`
to your local Maven repository. All example projects depend on this
artifact.

## How the Spec Is Consumed

Each consumer project declares a dependency on this artifact:

```kotlin
implementation("tech.sabai.contracteer.examples:musketeer-spec:1.0.0")
```

The specification file is on the classpath and accessible via
`classpath:musketeer-api.yaml` in both Contracteer annotations
and programmatic usage.

## Contracteer Features Demonstrated

The specification is designed to showcase how Contracteer
uses OpenAPI features. The YAML file includes inline
`[Contracteer]` comments that explain each feature in
context -- open it alongside this summary.

### Scenarios

Contracteer creates scenarios from OpenAPI example keys.
The following sections show the different mechanisms at work
in this specification.

#### Example keys and the intersection rule

`POST /musketeers` demonstrates the basic mechanism. The key
`D_ARTAGNAN_JOINS` appears on the request body and on the
`Location` response header. Contracteer intersects the two
key sets and creates one scenario that pairs the request
body (d'Artagnan, CADET, Rapier) with the response header
(`/musketeers/4`).

`GET /musketeers/{id}` shows multiple scenarios from one
operation. The keys `ATHOS` and `PORTHOS` appear on both the
path parameter and the response body, producing two
scenarios.

#### Correlated examples across parameters

A single example key can span multiple request elements.
`GET /musketeers/{id}/missions` uses the key
`ATHOS_MISSIONS` on both the path parameter `id` and the
query parameter `status`. Contracteer links them into one
scenario that sends
`GET /musketeers/1/missions?status=COMPLETED` and validates
the response.

#### Single `example` keyword

`POST /missions` uses the singular `example` keyword instead
of `examples`. Contracteer converts it to a synthetic key
`_example` and applies the standard intersection rule. The
same `example` keyword on the `Location` response header
correlates with the request body via that shared synthetic
key.

#### Status-code-prefixed example keys

Keys matching the pattern `{statusCode}_{description}`
(e.g. `404_UNKNOWN_MUSKETEER`) target a specific response
status code directly. These keys bypass the intersection
requirement -- the response does not need to have a matching
example. This enables scenarios for responses that have no
body or headers.

The request values in a status-code-prefixed 400 scenario
are intentionally invalid -- a wrong type, an enum value
outside the allowed set. Contracteer does not validate these
values against the request schema, because they are expected
to violate it.

Examples in this spec:

| Key | Target | Purpose |
|-----|--------|---------|
| `400_INVALID_MUSKETEER` | 400 | Invalid enum value (`KNIGHT` for rank) |
| `400_INVALID_ID` | 400 | Wrong type (string `abc` for integer) |
| `404_UNKNOWN_MUSKETEER` | 404 | Non-existent resource (id `999`) |
| `404_UNKNOWN_MISSION` | 404 | Non-existent resource (id `999`) |

### Schema-Only Fallback

`GET /musketeers` and `GET /missions` have no `examples`.
Contracteer generates a verification case from the schema
alone, using random values that conform to the declared
types and constraints. See the
[server example](../musketeer-spring-boot-server/) for how
this plays out in practice.

### Automatic Type-Mismatch 400 Verification

When an operation defines a `400` response, Contracteer
automatically generates verification cases that send
type-mismatched values (e.g. a string where an integer is
expected), even without explicit `400` examples. See the
[server example](../musketeer-spring-boot-server/) for
details.

### RFC 7807 Problem Details

Error responses use `application/problem+json` with a
`ProblemDetail` schema, demonstrating that Contracteer
handles different content types for success and error
responses on the same operation.
