plugins {
    `java-platform`
}

description = "Distributed lock dependency platform (BOM)"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":distributed-lock:core"))
        api(project(":distributed-lock:spring"))
        api(project(":distributed-lock:spring-boot-autoconfigure"))
        api(project(":distributed-lock:spring-boot-starter"))
        api(project(":distributed-lock:spring-boot-starter-redis"))
        api(project(":distributed-lock:spring-boot-starter-jdbc"))
        api(project(":distributed-lock:provider-redis"))
        api(project(":distributed-lock:provider-jdbc"))
        api(project(":distributed-lock:test"))
    }
}
