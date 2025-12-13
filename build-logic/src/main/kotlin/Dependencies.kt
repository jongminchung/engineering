/**
 * 중앙화된 의존성 버전 관리
 * - 모든 프로젝트와 build-logic에서 접근 가능
 * - 단일 진실 공급원(Single Source of Truth)
 */
object Versions {
    // Kafka
    const val KAFKA = "3.6.1"
    const val SPRING_KAFKA = "3.1.1"

    // Serialization
    const val JACKSON = "2.16.0"

    // Testcontainers
    const val TESTCONTAINERS = "1.20.4"

    // Database
    const val POSTGRESQL = "42.7.4"

    // Common Libraries
    const val COMMONS_TEXT = "1.13.0"

    // Logging
    const val SLF4J = "2.0.16"
    const val LOGBACK = "1.5.15"

    // Testing
    const val JUNIT = "6.0.1"
    const val ASSERTJ = "3.27.6"
    const val AWAITILITY = "4.2.0"
}

object Dependencies {
    const val SPRING_BOOT_STARTER = "org.springframework.boot:spring-boot-starter"
    const val SPRING_BOOT_STATER_DATA_JPA = "org.springframework.boot:spring-boot-starter-data-jpa"
    const val SPRING_BOOT_STARTER_TEST = "org.springframework.boot:spring-boot-starter-test"

    // Kafka
    const val KAFKA_CLIENT = "org.apache.kafka:kafka-clients:${Versions.KAFKA}"
    const val SPRING_KAFKA = "org.springframework.kafka:spring-kafka:${Versions.SPRING_KAFKA}"

    // Serialization
    const val JACKSON_DATA_BIND = "com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}"

    // Testcontainers
    const val TESTCONTAINERS_BOM = "org.testcontainers:testcontainers-bom:${Versions.TESTCONTAINERS}"
    const val TESTCONTAINERS_CORE = "org.testcontainers:testcontainers"
    const val TESTCONTAINERS_JUNIT_JUPITER = "org.testcontainers:junit-jupiter"
    const val TESTCONTAINERS_POSTGRESQL = "org.testcontainers:postgresql"
    const val TESTCONTAINERS_KAFKA = "org.testcontainers:kafka"

    // Database
    const val POSTGRESQL_DRIVER = "org.postgresql:postgresql:${Versions.POSTGRESQL}"

    // Common Libraries
    const val COMMONS_TEXT = "org.apache.commons:commons-text:${Versions.COMMONS_TEXT}"

    // Logging
    const val SLF4jAPI = "org.slf4j:slf4j-api:${Versions.SLF4J}"
    const val LOGBACK_CLASSIC = "ch.qos.logback:logback-classic:${Versions.LOGBACK}"

    // Testing
    const val ASSERTJ_CORE = "org.assertj:assertj-core:${Versions.ASSERTJ}"
    const val AWAITILITY = "org.awaitility:awaitility:${Versions.AWAITILITY}"
}
