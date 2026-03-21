# Contracteer Examples

Working examples that demonstrate how to use
[Contracteer](https://github.com/sabai-tech/contracteer)
for contract testing with OpenAPI specifications.

For concepts, getting started guides, and troubleshooting, see the
[Contracteer documentation](https://sabai-tech.github.io/contracteer/latest/).

## Which Example Is for Me?

All examples share a single OpenAPI specification
([musketeer-spec](musketeer-spec/)) as the source of
truth.

| I want to...                           | Contracteer module                           | Example                                                   |
|----------------------------------------|----------------------------------------------|-----------------------------------------------------------|
| **Verify my server** matches the spec  | `contracteer-verifier-junit`                 | [Spring Boot](musketeer-spring-boot-server/)              |
| **Test my client** with a mock server  | `contracteer-mockserver-spring`  | [Spring Boot](musketeer-spring-boot-client/)              |
|                                        | `contracteer-mockserver`                     | [Plain Java](java-musketeer-client/) (JUnit 5)            |

`contracteer-verifier-junit` is framework-agnostic -- it
works with any server that can be started in a JUnit 5
test. The Spring Boot example is just one implementation.

## Repository Structure

```
contracteer-examples/
  musketeer-spec/                  OpenAPI specification (shared)
  musketeer-spring-boot-server/    Server verified with contracteer-verifier-junit
  musketeer-spring-boot-client/    Client tested with contracteer-mockserver (Spring Boot)
  java-musketeer-client/           Client tested with contracteer-mockserver (plain Java)
```

## Prerequisites

- Java 21
- Contracteer available (from Maven Central or Maven Local)

## Getting Started

Each project is a standalone Gradle project with its own
`gradlew` wrapper. Before running any project, publish the
shared specification:

```bash
cd musketeer-spec
./gradlew publishToMavenLocal
```

Then follow the instructions in each project's README.

## License

These examples are licensed under the
[Apache License 2.0](LICENSE).
