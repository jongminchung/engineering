plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock Spring Boot starter (JDBC)"

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}"))

    api(project(":distributed-lock:spring-boot-autoconfigure"))
    api(project(":distributed-lock:provider-jdbc"))

    api("org.springframework.boot:spring-boot-starter")
}
