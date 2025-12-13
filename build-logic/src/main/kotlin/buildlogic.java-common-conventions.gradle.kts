@file:Suppress("UnstableApiUsage")

plugins {
    java
}

repositories {
    mavenCentral()
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter(libs.findVersion("junit").get().requiredVersion)

            // Shared assertion library for all projects
            dependencies {
                implementation(libs.findLibrary("assertj-core").get())
            }
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion.toInt())
    }
}
