# Java AOP (Aspect Oriented Programming)

AOP는 관점 지향 프로그래밍으로, 애플리케이션의 핵심 비즈니스 로직에서 반복되는 공통 관심사(Cross-cutting Concerns)를
분리하여 모듈화하는 프로그래밍 패러다임입니다.

## 1. AOP의 핵심 개념

핵심 로직(Core Concerns)과 공통 로직(Cross-cutting Concerns)을 분리함으로써 코드의 재사용성을 높이고 유지보수를
용이하게 합니다.

### 주요 용어 정리

- **Aspect**: 공통 관심사를 모듈화한 것 (Advice + Pointcut).
- **Target**: Aspect를 적용할 대상 (클래스, 메서드 등).
- **Advice**: 실질적으로 수행할 공통 로직 (언제 수행할지도 포함).
- **Join Point**: Advice가 적용될 수 있는 위치 (메서드 실행, 필드 접근 등). 자바(Spring)에서는 주로 메서드 실행
	시점.
- **Pointcut**: Join Point 중에서 실제로 Advice를 적용할 위치를 선정하는 지시자.
- **Weaving**: Pointcut에 의해 결정된 Target의 Join Point에 Advice를 삽입하는 과정.

---

## 2. Advice의 종류 (수행 시점)

- **Before**: 메서드 실행 전.
- **After**: 메서드 실행 후 (성공, 실패 상관없이).
- **After Returning**: 메서드가 성공적으로 결과를 반환한 후.
- **After Throwing**: 메서드 실행 중 예외가 발생한 후.
- **Around**: 메서드 실행 전후를 모두 제어 (가장 강력함).

---

## 3. 자바에서의 AOP 구현 방식

### 프록시 기반 AOP (Spring AOP)

- **JDK Dynamic Proxy**: 인터페이스가 있을 때 사용. Java의 `java.lang.reflect.Proxy` 사용.
- **CGLIB**: 인터페이스가 없을 때 사용. 대상 클래스의 서브클래스를 동적으로 생성.
- **특징**: 런타임에 프록시 객체를 생성하여 호출을 가로챕니다.

### 바이트코드 조작 (AspectJ)

- **CTW (Compile-Time Weaving)**: 컴파일 시점에 클래스 파일에 직접 코드를 삽입.
- **LTW (Load-Time Weaving)**: JVM에 클래스가 로드될 때 에이전트를 통해 바이트코드를 수정.
- **특징**: 메서드 호출 외에도 필드 수정, 생성자 호출 등 정교한 제어가 가능하며 성능이 더 뛰어납니다.

---

## 4. 실전 활용 사례

1. **트랜잭션 관리**: `@Transactional`을 통한 자동 Commit/Rollback 처리.
2. **로깅 및 모니터링**: 메서드 실행 시간 측정 및 파라미터 로깅.
3. **보안/인증**: 특정 API 호출 전 권한 검사.
4. **캐싱**: 결과를 캐시에 저장하고 재사용.

---

## 5. 요약: OOP와의 관계

AOP는 OOP를 대체하는 것이 아니라 보완하는 개념입니다. OOP가 객체를 통해 비즈니스 로직을 수직적으로 구성한다면, AOP는 이를 가로질러
발생하는 반복 코드를 수평적으로 관리할 수 있게 해줍니다.
