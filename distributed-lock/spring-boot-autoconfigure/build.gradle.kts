plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock Spring Boot autoconfiguration"

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}"))
    api(platform(libs.testcontainers.bom))

    api(project(":distributed-lock:spring"))

    compileOnly(project(":distributed-lock:provider-redis"))
    compileOnly(project(":distributed-lock:provider-jdbc"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation(libs.redisson)
}
