/*
 * Kafka 학습용 컨벤션 플러그인
 * - Kafka Client 및 Spring Kafka 의존성
 * - TestContainers를 이용한 통합 테스트 환경
 * - JSON 직렬화 및 로깅 설정
 */

plugins {
    id("buildlogic.java-common-conventions")
    `java-library`
}

dependencies {
    // Kafka Client
    implementation(Dependencies.KAFKA_CLIENT)

    // Spring Kafka (Optional)
    implementation(Dependencies.SPRING_KAFKA)

    // JSON 직렬화
    implementation(Dependencies.JACKSON_DATA_BIND)

    // Logging
    implementation(Dependencies.SLF4jAPI)
    runtimeOnly(Dependencies.LOGBACK_CLASSIC)

    // TestContainers
    testImplementation(platform(Dependencies.TESTCONTAINERS_BOM))
    testImplementation(Dependencies.TESTCONTAINERS_CORE)
    testImplementation(Dependencies.TESTCONTAINERS_KAFKA)
    testImplementation(Dependencies.TESTCONTAINERS_JUNIT_JUPITER)

    // Test Utilities
    testImplementation(Dependencies.AWAITILITY)
}

// 테스트 설정
tasks.test {
    useJUnitPlatform()

    // TestContainers를 위한 시스템 속성
    systemProperty("testcontainers.reuse.enable", "false")

    // 테스트 로그 출력
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Docker Compose 태스크 정의
tasks.register("dockerComposeUp", Exec::class) {
    group = "kafka"
    description = "Start Kafka cluster with Docker Compose"
    workingDir = projectDir
    commandLine("docker-compose", "-f", "docker-compose.kafka.yml", "up", "-d")
}

tasks.register("dockerComposeDown", Exec::class) {
    group = "kafka"
    description = "Stop Kafka cluster"
    workingDir = projectDir
    commandLine("docker-compose", "-f", "docker-compose.kafka.yml", "down")
}

tasks.register("dockerComposeDownVolumes", Exec::class) {
    group = "kafka"
    description = "Stop Kafka cluster and remove volumes"
    workingDir = projectDir
    commandLine("docker-compose", "-f", "docker-compose.kafka.yml", "down", "-v")
}

tasks.register("dockerComposeLogs", Exec::class) {
    group = "kafka"
    description = "Show Kafka logs"
    workingDir = projectDir
    commandLine("docker-compose", "-f", "docker-compose.kafka.yml", "logs", "-f")
}

tasks.register<Exec>("kafkaUI") {
    group = "kafka"
    description = "Open Kafka UI in browser"
    doFirst {
        val os = System.getProperty("os.name").lowercase()
        val cmd = when {
            os.contains("mac") -> listOf("open", "http://localhost:8080")
            os.contains("nix") || os.contains("nux") -> listOf("xdg-open", "http://localhost:8080")
            os.contains("win") -> listOf("cmd", "/c", "start", "http://localhost:8080")
            else -> throw GradleException("Unsupported OS: $os")
        }
        commandLine = cmd
    }
}
