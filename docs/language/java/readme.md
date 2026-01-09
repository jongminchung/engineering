# Java Engineering

대규모 시스템을 설계하고 운영하는 시니어 개발자가 반드시 갖추어야 할 핵심 지식과 JVM의 내부 동작 메커니즘을 정리합니다.

## 목차

### 1. [Classpath](classpath.md)

- Classpath 설정 방법 및 주요 특징
- 디렉토리 및 JAR 파일 구성 요소

### 2. [Java 예외 처리 (Exception Handling)](exception-handling.md)

- 예외 계층 구조 (Checked vs Unchecked)
- Checked Exception 처리 전략 및 현대적인 트렌드

### 3. [JVM 및 가비지 컬렉션](jvm.md)

- JVM 내부 구조 (클래스 로더, JIT 컴파일러, JMM)
- GraalVM Native Image
- 현대적 가비지 컬렉션 (G1 GC, ZGC) 및 튜닝

### 4. [어노테이션 및 코드 생성](annotation.md)

- 어노테이션 기본 규약 및 메타 어노테이션
- Annotation Processor와 Lombok의 동작 원리
- MapStruct, QueryDSL 등 코드 생성 라이브러리

### 5. [리플렉션 및 동적 프로그래밍](reflection.md)

- 리플렉션의 활용 (DI, AOP)
- 실전 코드 예시

### 6. [AOP (Aspect Oriented Programming)](aop.md)

- AOP 핵심 개념 및 용어
- Advice 종류 및 구현 방식 (Proxy vs AspectJ)

### 7. [Sealed Class](sealed-class.md)

- Sealed Class 등장 배경 및 컴파일러 힌트
- Exhaustiveness Check 및 패턴 매칭 활용

### 8. [Java Modules & Module Import](modules.md)

- Project Jigsaw (Java 9) 핵심 개념
- `module-info.java` 구조 및 강력한 캡슐화
- Java 23 모듈 임포트 (JEP 476)

### 9. [현대 자바의 진화](modern-java.md)

- Java 17, 21, 25 주요 특징
- Virtual Threads, Project Valhalla 등

### 10. [아키텍처 및 실전 예제](architecture.md)

- 객체지향 설계와 SOLID
- 레이어드 아키텍처
- 동시성 제어 및 스트림 API 예제

### 11. [시니어 개발자 체크리스트](checklist.md)

- 성능, 패턴, 테스트, 빌드 시스템 점검 항목
