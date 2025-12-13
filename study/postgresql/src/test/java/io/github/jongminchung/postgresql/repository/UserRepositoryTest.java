package io.github.jongminchung.postgresql.repository;

import io.github.jongminchung.postgresql.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Boot 4.0 + Testcontainers 1.20 기반 PostgreSQL 통합 테스트
 *
 * 베스트 프랙티스:
 * 1. @Testcontainers - JUnit 5 확장을 통한 자동 컨테이너 관리
 * 2. @Container - static 필드로 선언하여 모든 테스트에서 컨테이너 재사용
 * 3. @DynamicPropertySource - 동적으로 데이터소스 연결 정보 주입
 * 4. @SpringBootTest - Spring Boot 전체 컨텍스트 로드
 * 5. @Transactional - 각 테스트 후 롤백
 */
@SpringBootTest
@Testcontainers
@Transactional
class UserRepositoryTest {

    /**
     * PostgreSQL 컨테이너 설정
     * - static 필드로 선언하여 테스트 클래스의 모든 테스트 메서드에서 재사용
     * - withReuse(true) 설정으로 로컬 개발 시 컨테이너 재사용 (성능 향상)
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    /**
     * Spring의 DynamicPropertySource를 사용하여 컨테이너의 동적 연결 정보를 주입
     * 이 방식은 Spring Boot 2.2.6+ 부터 지원되며, 가장 권장되는 방법
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testContainerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void testSaveAndFindUser() {
        // Given
        User user = new User("testuser", "test@example.com");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = new User("john", "john@example.com");
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("john");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testExistsByEmail() {
        // Given
        User user = new User("jane", "jane@example.com");
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("jane@example.com");
        boolean notExists = userRepository.existsByEmail("notfound@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = new User("bob", "bob@example.com");
        User savedUser = userRepository.save(user);

        // When
        savedUser.setEmail("newemail@example.com");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
    }

    @Test
    void testDeleteUser() {
        // Given
        User user = new User("alice", "alice@example.com");
        User savedUser = userRepository.save(user);

        // When
        userRepository.deleteById(savedUser.getId());

        // Then
        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        // Given
        userRepository.save(new User("user1", "user1@example.com"));
        userRepository.save(new User("user2", "user2@example.com"));
        userRepository.save(new User("user3", "user3@example.com"));

        // When
        var users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(3);
    }
}
