import org.gradle.kotlin.dsl.annotationProcessor

plugins {
    java
    id("org.springframework.boot") version "3.4.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.netflix.dgs.codegen") version "8.1.0"
}

group = "org.newtco.obserra"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }

    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "--enable-preview",
            "-parameters"
        )
    )
}


repositories {
    mavenCentral()
}

val mockitoAgent: org.gradle.api.artifacts.Configuration by configurations.creating

configurations {

    compileOnly {
        extendsFrom(mockitoAgent)
    }

    testImplementation {
        extendsFrom(mockitoAgent)
    }
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:10.1.2")
    }
}


dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Database dependencies removed - using in-memory storage instead

    // GraphQL dependencies - Netflix DGS Framework
    // https://mvnrepository.com/artifact/com.netflix.graphql.dgs/graphql-dgs-platform-dependencies
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    // Obserra shared module
    implementation(project(":obserra-shared"))

    // Kubernetes client
    implementation("io.kubernetes:client-java:23.0.0")

    // JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter-test")


    mockitoAgent("org.mockito:mockito-core:5.17.0") {
        isTransitive = false
    }
}

tasks.generateJava {
    schemaPaths.add("${projectDir}/src/main/resources/graphql-client")
    packageName = "org.newtco.obserra.graphql.client"
    generateClient = true
}

tasks.withType<Test> {
    useJUnitPlatform()

    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--enable-preview",
            "-javaagent:${mockitoAgent.singleFile}",
            "-Djunit.jupiter.extensions.autodetection.enabled=true",
            "-Xshare:off",

            )
    })
}
