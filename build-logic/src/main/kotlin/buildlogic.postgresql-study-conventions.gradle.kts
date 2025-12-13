plugins {
    id("buildlogic.testcontainers-conventions")
}

dependencies {
    // PostgreSQL Driver
    runtimeOnly(Dependencies.POSTGRESQL_DRIVER)

    // Testcontainers PostgreSQL Module
    testImplementation(Dependencies.TESTCONTAINERS_POSTGRESQL)
}
