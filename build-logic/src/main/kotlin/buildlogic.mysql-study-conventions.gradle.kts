plugins {
    id("buildlogic.testcontainers-conventions")
}

dependencies {
    // PostgreSQL Driver
    runtimeOnly(buildlogicLibs.findLibrary("mysql").get())

    testImplementation(buildlogicLibs.findLibrary("testcontainers-mysql").get())

    // Flyway
    testImplementation("org.springframework.boot:spring-boot-starter-flyway")
    testImplementation("org.flywaydb:flyway-mysql")
}
