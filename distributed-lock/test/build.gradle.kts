plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock test utilities"

dependencies {
    api(project(":distributed-lock:core"))
    api(platform(libs.testcontainers.bom))

    api(libs.testcontainers.core)
}
