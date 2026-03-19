plugins {
    java
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
    // OpenAPI specification
    implementation("tech.sabai.contracteer.examples:musketeer-spec:1.0.0")

    // JSON serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")

    testImplementation("tech.sabai.contracteer:contracteer-mockserver:2.0.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.16")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
