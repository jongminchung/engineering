# 대규모 프로젝트 설계를 위한 아키텍처 원칙

대규모 시스템을 설계할 때는 유지보수성과 확장성을 고려한 아키텍처 원칙이 중요합니다.

## 1. 객체지향 설계와 SOLID

- **SRP (Single Responsibility Principle)**: 클래스는 단 하나의 변경 이유만을 가져야 합니다.
- **DIP (Dependency Inversion Principle)**: "고수준 모듈은 저수준 모듈에 의존해서는 안 된다. 둘 다 추상화에 의존해야 한다." Spring Framework의 핵심인 DI(Dependency Injection)의 근간입니다.

## 2. 모듈화 및 레이어드 아키텍처

대규모 프로젝트는 계층별 책임 분리가 필수입니다.

- **Domain Layer**: 비즈니스 로직의 핵심 (POJO 기반으로 외부 프레임워크 의존성 최소화).
- **Infrastructure Layer**: DB, 외부 API 연동 등.
- **Application Layer**: 유스케이스 흐름 제어.

---

## 실전 코드 예시: 동시성 제어와 함수형 프로그래밍

### 1. 멀티스레드 환경의 안전한 데이터 처리

대규모 요청 처리 시 원자성(Atomicity) 보장이 중요합니다.

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyExample {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void safeIncrement() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::incrementAndGet);
        }
        executor.shutdown();
    }
}
```

### 2. 스트림 API를 통한 선언적 데이터 처리

```java
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public List<String> getActiveUserNames(List<User> users) {
        return users.stream()
            .filter(User::isActive)
            .map(User::getName)
            .sorted()
            .collect(Collectors.toList());
    }
}
```
