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
    implementation(libs.findLibrary("spring-boot-starter").get())
    implementation(libs.findLibrary("spring-boot-starter-data-jpa").get())

    testImplementation(platform(libs.findLibrary("testcontainers-bom").get()))

    testImplementation(libs.findBundle("testcontainers").get())

    // Spring Boot Test
    testImplementation(libs.findLibrary("spring-boot-starter-test").get()) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
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
