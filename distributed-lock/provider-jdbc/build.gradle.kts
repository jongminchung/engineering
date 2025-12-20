plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock JDBC provider"

dependencies {
    api(project(":distributed-lock:core"))

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.mysql)
}
