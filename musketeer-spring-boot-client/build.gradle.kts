plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "tech.sabai.contracteer.examples"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // spring-boot-starter-web provides RestClient for HTTP calls.
    // The embedded server is disabled via spring.main.web-application-type=none
    // in application.yml — this project is a pure client, not a server.
    implementation("org.springframework.boot:spring-boot-starter-web")

    // OpenAPI specification
    implementation("tech.sabai.contracteer.examples:musketeer-spec:1.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("tech.sabai.contracteer:contracteer-mockserver-spring:2.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
