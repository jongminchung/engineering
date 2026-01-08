# Java Engineering

대규모 시스템을 설계하고 운영하는 시니어 개발자가 반드시 갖추어야 할 핵심 지식과 JVM의 내부 동작
메커니즘을 상세히 정리합니다.

---

## 1. JVM (Java Virtual Machine) 및 GraalVM의 심층 내부 구조

자바는 단순한 언어가 아니라, **컴파일 타임 환경**과 **런타임 환경**이 철저히 분리된 생태계입니다.

특히 최근에는 기존의 JIT 기반 JVM을 넘어 **GraalVM Native Image**를 통한 AOT(Ahead-Of-Time)

컴파일이 대규모 프로젝트의 핵심 기술로 자리잡고 있습니다.

### 1.1. 클래스 로더 서브시스템 (Class Loader Subsystem)

클래스 로더는 런타임에 클래스 파일(`.class`)을 동적으로 로드하고 링크하며 초기화합니다.

- **Loading**: 클래스를 읽어오며, 세 가지 계층 구조(Bootstrap -> Extension/Platform ->
	Application)
- **Linking**: 로드된 클래스의 유효성을 검증(Verify), 정적 변수 메모리 준비(Prepare), 심볼릭 레퍼런스를 직접
	레퍼런스로 교체(Resolve).
- **Initialization**: 클래스 변수들을 적절한 값으로 초기화.

### 1.2. JIT (Just-In-Time) 컴파일러의 마법

JVM은 인터프리터 방식과 컴파일 방식을 혼합하여 사용합니다.

- **인터프리터**: 바이트코드를 한 줄씩 해석하여 즉시 실행 (초기 실행 속도 빠름).
- **JIT 컴파일러**: 반복적으로 실행되는 코드(Hot Spot)를 감지하여 원시 기계어(Native Code)로 컴파일하고 캐싱합니다 (
	전체 실행 성능 극대화).
- **Optimization**: 인라이닝(Inlining), 루프 펼치기(Loop Unrolling), 가상 메서드 호출 최적화 등을
	수행합니다.

### 1.3. JVM 메모리 모델 (JMM)과 TLAB

- **TLAB (Thread-Local Allocation Buffer)**: 멀티스레드 환경에서 객체 생성 시의 경합(Lock
	Contention)을 줄이기 위해 각 스레드마다 전용 힙 영역을 할당하는 최적화 기법입니다.
- **Stack vs Heap**: 기본형 변수와 객체 참조는 Stack에, 실제 객체 인스턴스는 Heap에 저장되는 구조를 명확히 이해해야
	메모리 누수(Memory Leak)를 추적할 수 있습니다.

### 1.4. Native Program vs JVM vs GraalVM Native Image

| 특징          | Native Program (C/C++) | Standard JVM (HotSpot) | GraalVM Native Image          |
|:------------|:-----------------------|:-----------------------|:------------------------------|
| **컴파일 방식**  | AOT (전체 바이너리 생성)       | JIT (런타임 컴파일)          | AOT (네이티브 실행 파일 생성)           |
| **플랫폼 독립성** | 낮음 (빌드 타임 결정)          | 높음 (WORA)              | 낮음 (빌드 타임 결정)                 |
| **시작 속도**   | 매우 빠름                  | 느림 (JVM 로딩 + JIT 웜업)   | 매우 빠름 (즉각 실행)                 |
| **메모리 점유**  | 낮음                     | 높음 (JVM 오버헤드)          | 매우 낮음                         |
| **런타임 최적화** | 고정됨                    | 동적 (실행 중 최적화)          | 고정됨 (Closed-world assumption) |

#### GraalVM Native Image의 핵심: Closed-World Assumption

GraalVM은 실행 시점에 모든 클래스가 이미 존재한다고 가정합니다. 이는 **리플렉션, 동적 프록시** 등을 빌드 타임에 미리 분석해야 함을
의미하며, 이를 통해 런타임 시 불필요한 메타데이터와 코드를 제거하여 배포 이미지 크기를 획기적으로 줄입니다.

---

## 2. 현대적 가비지 컬렉션 (Advanced GC)

대규모 프로젝트에서는 지연 시간(Latency)과 처리량(Throughput) 사이의 트레이드오프를 결정해야 합니다.

### 2.1. GC 알고리즘의 진화

- **G1 GC (Java 9+ Default)**: 힙을 Region으로 나누어 점진적으로 수집.
- **ZGC (Java 15+ Production Ready, Java 21+ Generational ZGC)**: 테라바이트 급의 힙에서도
	중단 시간(Stop-The-World)을 1ms 이하로 유지. Java 21에서는 세대별(Generational) 관리 기능이 추가되어
	효율성이 더 높아졌습니다.

### 2.2. GC 성능 튜닝과 JVM 옵션

대규모 시스템 최적화를 위한 필수 JVM 옵션들입니다.

- **메모리 설정**:
 	- `-Xms`, `-Xmx`: 힙 메모리의 최소/최대 크기 설정.
 	- `-XX:MetaspaceSize`, `-XX:MaxMetaspaceSize`: 클래스 메타데이터 저장 영역 크기.
- **GC 선택**:
 	- `-XX:+UseG1GC`: G1 GC 사용.
 	- `-XX:+UseZGC -XX:+ZGenerational`: Java 21의 세대별 ZGC 사용.
- **디버깅/로그**:
 	- `-Xlog:gc*`: GC 로그 상세 출력.
 	- `-XX:+HeapDumpOnOutOfMemoryError`: OOM 발생 시 힙 덤프 생성.

## 3. 리플렉션 (Reflection)과 동적 프로그래밍

리플렉션은 실행 중인 자바 프로그램이 자신의 내부 구조를 검사하고 수정할 수 있게 해주는 강력한 기능입니다. Spring, Hibernate
같은 대규모 프레임워크의 근간이 됩니다.

### 3.1. 리플렉션의 활용

- **의존성 주입 (DI)**: 런타임에 객체를 생성하고 private 필드에 의존성을 주입합니다.
- **프록시 기반 AOP**: 인터페이스나 클래스를 동적으로 구현하여 트랜잭션, 로깅 등을 처리합니다.
- **동적 로딩**: 설정 파일 등에 명시된 클래스 이름을 기반으로 클래스를 로드합니다.

### 3.2. 실전 코드 예시: 리플렉션으로 Private 필드 접근

```java
import java.lang.reflect.Field;

public class ReflectionExample {
	static void main(String[] args) throws Exception {
		User user = new User("Old Name");

		// 클래스 정보 획득
		Class<?> clazz = user.getClass();

		// Private 필드 접근 및 수정
		Field nameField = clazz.getDeclaredField("name");
		nameField.setAccessible(true); // Private 접근 허용
		nameField.set(user, "New Name via Reflection");

		System.out.println(user.getName());
	}
}
```

---

## 4. 빌드 시스템과 배포: 클래스패스(Classpath)와 메니페스트(Manifest)

애플리케이션이 실행되기 위해서는 필요한 모든 클래스 파일과 라이브러리가 어디에 있는지 JVM에 알려줘야 합니다.

### 4.1. 클래스패스 (Classpath, `-cp`)

JVM이 프로그램 실행에 필요한 `.class` 파일이나 `.jar` 파일을 찾는 경로의 집합입니다.

- **환경 변수**: `CLASSPATH` 변수에 설정.
- **실행 옵션**: `java -cp "lib/*:bin" com.example.Main` 처럼 직접 지정 (추천 방식).
- **우선순위**: 클래스 로더 계층 구조에 따라 부모 로더가 찾지 못한 경우 클래스패스에서 탐색합니다.

### 4.2. JAR 메니페스트 (META-INF/MANIFEST.MF)

JAR 파일의 메타데이터를 담고 있는 특수 파일입니다.

- **Main-Class**: `java -jar app.jar` 명령 실행 시 진입점이 될 클래스 지정.
- **Class-Path**: 해당 JAR가 의존하는 다른 JAR 파일들의 경로를 정의.
- **Implementation-Version**: 라이브러리의 버전 정보 등을 저장.

---

## 5. 현대 자바의 진화 (Java 17, 21, 25)

자바는 6개월 주기의 릴리스와 LTS(Long-Term Support) 모델을 통해 빠르게 발전하고 있습니다.

### 5.1. Java 17 LTS: 현대 자바의 표준화

- **Sealed Classes**: 상속 가능한 클래스를 명시적으로 제한하여 도메인 모델의 무결성을 보장합니다.
- **Records**: 불변(Immutable) 데이터를 운반하는 클래스를 간결하게 정의합니다.
- **Pattern Matching for switch (Preview)**: 복잡한 타입 체크와 캐스팅을 가독성 있게 처리합니다.

### 5.2. Java 21 LTS: 대규모 시스템의 혁신

- **Virtual Threads (Project Loom)**: OS 스레드에 종속되지 않는 경량 스레드를 도입하여, 수만 개의 동시 요청을
	최소한의 자원으로 처리할 수 있게 합니다. 블로킹 I/O 기반 코드를 논블로킹처럼 효율적으로 실행합니다.
- **Sequenced Collections**: 순서가 있는 컬렉션(List, Deque 등)에 대한 공통 인터페이스를 제공하여 일관된
	접근을 가능케 합니다.
- **Record Patterns**: 레코드의 컴포넌트를 분해하여 패턴 매칭에 활용할 수 있습니다.

### 5.3. Java 25 LTS (예정 및 방향성)

- **Project Valhalla (Value Objects)**: 객체의 정체성(Identity)을 제거하고 데이터만 가짐으로써 메모리
	레이아웃을 최적화하고 캐시 효율을 극대화합니다.
- **Project Panama**: 네이티브 코드(C/C++)와의 연결을 더 빠르고 안전하게 만들어, 기존 JNI의 복잡성과 성능 저하를
	해결합니다.

---

## 6. 대규모 프로젝트 설계를 위한 아키텍처 원칙

### 5.1. 객체지향 설계와 SOLID

- **SRP (Single Responsibility Principle)**: 클래스는 단 하나의 변경 이유만을 가져야 합니다.
- **DIP (Dependency Inversion Principle)**: "고수준 모듈은 저수준 모듈에 의존해서는 안 된다. 둘 다
	추상화에 의존해야 한다." Spring Framework의 핵심인 DI(Dependency Injection)의 근간입니다.

### 5.2. 모듈화 및 레이어드 아키텍처

대규모 프로젝트는 계층별 책임 분리가 필수입니다.

- **Domain Layer**: 비즈니스 로직의 핵심 (POJO 기반으로 외부 프레임워크 의존성 최소화).
- **Infrastructure Layer**: DB, 외부 API 연동 등.
- **Application Layer**: 유스케이스 흐름 제어.

---

## 6. 실전 코드 예시: 동시성 제어와 함수형 프로그래밍

### 6.1. 멀티스레드 환경의 안전한 데이터 처리

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

### 6.2. 스트림 API를 통한 선언적 데이터 처리

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

---

## 7. 시니어 개발자의 체크리스트

- [ ] **성능 프로파일링**: JProfiler, VisualVM 등을 사용해 병목 지점을 찾을 수 있는가?
- [ ] **디자인 패턴**: 상황에 맞는 디자인 패턴(Factory, Strategy, Proxy 등)을 적절히 적용하는가?
- [ ] **테스트 코드**: 단위 테스트(JUnit5), 통합 테스트를 통해 코드의 안정성을 확보하는가?
- [ ] **빌드 시스템**: Gradle이나 Maven의 복잡한 멀티 모듈 설정을 이해하고 관리하는가?
