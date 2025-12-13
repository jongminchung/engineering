plugins {
    id("buildlogic.java-common-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters (버전은 Spring Boot Plugin이 관리)
    implementation(Dependencies.SPRING_BOOT_STARTER)
    implementation(Dependencies.SPRING_BOOT_STATER_DATA_JPA)

    // Testcontainers BOM (Bill of Materials)
    testImplementation(platform(Dependencies.TESTCONTAINERS_BOM))

    // Testcontainers Core
    testImplementation(Dependencies.TESTCONTAINERS_CORE)
    testImplementation(Dependencies.TESTCONTAINERS_JUNIT_JUPITER)

    // Spring Boot Test
    testImplementation(Dependencies.SPRING_BOOT_STARTER_TEST) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Logging
    implementation(Dependencies.SLF4jAPI)
    runtimeOnly(Dependencies.LOGBACK_CLASSIC)
}


// 테스트 설정
tasks.test {
    useJUnitPlatform()

    // TestContainers 최적 설정
    systemProperty("testcontainers.reuse.enable", "true")
    systemProperty("testcontainers.image.substitutor", "org.testcontainers.utility.ImageSubstitutor")

    // 병렬 테스트 실행 (선택 사항)
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

    // 테스트 로깅
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    // 메모리 설정
    minHeapSize = "512m"
    maxHeapSize = "2g"
}

// 테스트 리포트 설정
tasks.withType<Test> {
    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }
}
