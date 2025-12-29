plugins {
    `kotlin-dsl`
    `maven-publish`
    `version-catalog`
    id("com.gradle.plugin-publish") version "1.3.0"
}

group = "io.github.jongminchung"
version = "0.0.1"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management.plugin)
    implementation(libs.spotless.plugin)
    implementation(libs.spotless.convention.plugin)
}

configure<CatalogPluginExtension> {
    versionCatalog {
        from(files("src/main/resources/META-INF/gradle/buildlogic-libs.versions.toml"))
    }
}

gradlePlugin {
    website.set("https://github.com/jongminchung/engineering")
    vcsUrl.set("https://github.com/jongminchung/engineering.git")

    plugins {
        named("buildlogic.java-common-conventions") {
            displayName = "Java Common Conventions"
            description = "Common conventions for Java projects including toolchain and libraries"
            tags.set(listOf("java", "conventions"))
        }
        named("buildlogic.java-library-conventions") {
            displayName = "Java Library Conventions"
            description = "Conventions for Java library projects"
            tags.set(listOf("java", "library"))
        }
        named("buildlogic.java-test-conventions") {
            displayName = "Java Test Conventions"
            description = "Conventions for Java testing and Jacoco coverage"
            tags.set(listOf("java", "test", "jacoco"))
        }
        named("buildlogic.spring-boot-conventions") {
            displayName = "Spring Boot Conventions"
            description = "Conventions for Spring Boot projects with Lombok and testing"
            tags.set(listOf("spring", "spring-boot"))
        }
        named("buildlogic.testcontainers-conventions") {
            displayName = "Testcontainers Conventions"
            description = "Conventions for Testcontainers and optimized test execution"
            tags.set(listOf("test", "testcontainers"))
        }
        named("buildlogic.mysql-study-conventions") {
            displayName = "MySQL Study Conventions"
            description = "Conventions for MySQL database study with Testcontainers"
            tags.set(listOf("mysql", "testcontainers"))
        }
        named("buildlogic.kafka-study-conventions") {
            displayName = "Kafka Study Conventions"
            description = "Conventions for Kafka messaging study with Testcontainers"
            tags.set(listOf("kafka", "testcontainers"))
        }
        named("buildlogic.odata-library-conventions") {
            displayName = "OData Library Conventions"
            description = "Conventions for OData library development"
            tags.set(listOf("odata", "library"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            from(components["versionCatalog"])
            artifactId = "build-logic-catalog"
        }
    }
}
