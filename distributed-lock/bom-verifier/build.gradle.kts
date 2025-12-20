plugins {
    id("buildlogic.java-library-conventions")
}

description = "Distributed lock BOM verification"

val bomVerification by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    bomVerification(enforcedPlatform(project(":distributed-lock:dependencies")))

    bomVerification(project(":distributed-lock:core"))
    bomVerification(project(":distributed-lock:provider-redis"))
    bomVerification(project(":distributed-lock:provider-jdbc"))
    bomVerification(project(":distributed-lock:spring"))
    bomVerification(project(":distributed-lock:spring-boot-autoconfigure"))
    bomVerification(project(":distributed-lock:spring-boot-starter"))
    bomVerification(project(":distributed-lock:spring-boot-starter-redis"))
    bomVerification(project(":distributed-lock:spring-boot-starter-jdbc"))
    bomVerification(project(":distributed-lock:test"))
}

tasks.register("verifyBom") {
    group = "verification"
    description = "Resolves the distributed-lock BOM constraints against all modules."
    notCompatibleWithConfigurationCache("Resolves project dependencies for BOM verification.")
    doLast {
        bomVerification.resolve()
    }
}

tasks.named("check") {
    dependsOn("verifyBom")
}
