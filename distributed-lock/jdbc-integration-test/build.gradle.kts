plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock JDBC integration tests"

dependencies {
    testImplementation(project(":distributed-lock:spring-boot-starter-jdbc"))
    testImplementation(project(":distributed-lock:test"))

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.mysql)
    testImplementation("org.springframework.boot:spring-boot-test")
}
