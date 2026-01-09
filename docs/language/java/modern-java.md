# 현대 자바의 진화 (Java 17, 21, 25)

자바는 6개월 주기의 릴리스와 LTS(Long-Term Support) 모델을 통해 빠르게 발전하고 있습니다.

## 1. Java 17 LTS: 현대 자바의 표준화

- **Sealed Classes**: 상속 가능한 클래스를 명시적으로 제한하여 도메인 모델의 무결성을 보장합니다.
- **Records**: 불변(Immutable) 데이터를 운반하는 클래스를 간결하게 정의합니다.
- **Pattern Matching for switch (Preview)**: 복잡한 타입 체크와 캐스팅을 가독성 있게 처리합니다.

## 2. Java 21 LTS: 대규모 시스템의 혁신

- **Virtual Threads (Project Loom)**: OS 스레드에 종속되지 않는 경량 스레드를 도입하여, 수만 개의 동시 요청을
	최소한의 자원으로 처리할 수 있게 합니다. 블로킹 I/O 기반 코드를 논블로킹처럼 효율적으로 실행합니다.
- **Sequenced Collections**: 순서가 있는 컬렉션(List, Deque, Set 등)에 대한 공통 인터페이스(
	`SequencedCollection`, `SequencedSet`, `SequencedMap`)를 제공합니다. `reversed()`
	메서드를 통해 데이터 복사 없이 역순 뷰를 즉시 얻을 수 있습니다.
- **Record Patterns**: 레코드의 컴포넌트를 분해하여 패턴 매칭에 활용할 수 있습니다.

## 3. Java 25 LTS (예정 및 방향성)

- **Project Valhalla (Value Objects)**: 객체의 정체성(Identity)을 제거하고 데이터만 가짐으로써 메모리
	레이아웃을 최적화하고 캐시 효율을 극대화합니다.
- **Project Panama**: 네이티브 코드(C/C++)와의 연결을 더 빠르고 안전하게 만들어, 기존 JNI의 복잡성과 성능 저하를
	해결합니다.
