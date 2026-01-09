# JVM (Java Virtual Machine) 및 GraalVM 심층 내부 구조

자바는 단순한 언어가 아니라, **컴파일 타임 환경**과 **런타임 환경**이 철저히 분리된 생태계입니다. 특히 최근에는 기존의 JIT 기반
JVM을 넘어 **GraalVM Native Image**를 통한 AOT(Ahead-Of-Time) 컴파일이 대규모 프로젝트의 핵심 기술로
자리잡고 있습니다.

## 1. 클래스 로더 서브시스템 (Class Loader Subsystem)

클래스 로더는 런타임에 클래스 파일(`.class`)을 동적으로 로드하고 링크하며 초기화합니다.

- **Loading**: 클래스를 읽어오며, 세 가지 계층 구조(Bootstrap -> Extension/Platform ->
	Application)
- **Linking**: 로드된 클래스의 유효성을 검증(Verify), 정적 변수 메모리 준비(Prepare), 심볼릭 레퍼런스를 직접
	레퍼런스로 교체(Resolve).
- **Initialization**: 클래스 변수들을 적절한 값으로 초기화.

## 2. JIT (Just-In-Time) 컴파일러의 마법

JVM은 인터프리터 방식과 컴파일 방식을 혼합하여 사용합니다.

- **인터프리터**: 바이트코드를 한 줄씩 해석하여 즉시 실행 (초기 실행 속도 빠름).
- **JIT 컴파일러**: 반복적으로 실행되는 코드(Hot Spot)를 감지하여 원시 기계어(Native Code)로 컴파일하고 캐싱합니다 (
	전체 실행 성능 극대화).
- **Optimization**: 인라이닝(Inlining), 루프 펼치기(Loop Unrolling), 가상 메서드 호출 최적화 등을
	수행합니다.

## 3. JVM 메모리 모델 (JMM)과 TLAB

- **TLAB (Thread-Local Allocation Buffer)**: 멀티스레드 환경에서 객체 생성 시의 경합(Lock
	Contention)을 줄이기 위해 각 스레드마다 전용 힙 영역을 할당하는 최적화 기법입니다.
- **Stack vs Heap**: 기본형 변수와 객체 참조는 Stack에, 실제 객체 인스턴스는 Heap에 저장되는 구조를 명확히 이해해야
	메모리 누수(Memory Leak)를 추적할 수 있습니다.

## 4. Native Program vs JVM vs GraalVM Native Image

| 특징          | Native Program (C/C++) | Standard JVM (HotSpot) | GraalVM Native Image          |
|:------------|:-----------------------|:-----------------------|:------------------------------|
| **컴파일 방식**  | AOT (전체 바이너리 생성)       | JIT (런타임 컴파일)          | AOT (네이티브 실행 파일 생성)           |
| **플랫폼 독립성** | 낮음 (빌드 타임 결정)          | 높음 (WORA)              | 낮음 (빌드 타임 결정)                 |
| **시작 속도**   | 매우 빠름                  | 느림 (JVM 로딩 + JIT 웜업)   | 매우 빠름 (즉각 실행)                 |
| **메모리 점유**  | 낮음                     | 높음 (JVM 오버헤드)          | 매우 낮음                         |
| **런타임 최적화** | 고정됨                    | 동적 (실행 중 최적화)          | 고정됨 (Closed-world assumption) |

### GraalVM Native Image의 핵심: Closed-World Assumption

GraalVM은 실행 시점에 모든 클래스가 이미 존재한다고 가정합니다. 이는 **리플렉션, 동적 프록시** 등을 빌드 타임에 미리 분석해야 함을
의미하며, 이를 통해 런타임 시 불필요한 메타데이터와 코드를 제거하여 배포 이미지 크기를 획기적으로 줄입니다.

---

## 현대적 가비지 컬렉션 (Advanced GC)

대규모 프로젝트에서는 지연 시간(Latency)과 처리량(Throughput) 사이의 트레이드오프를 결정해야 합니다.

### 1. GC 알고리즘의 진화

- **G1 GC (Java 9+ Default)**: 힙을 Region으로 나누어 점진적으로 수집.
- **ZGC (Java 15+ Production Ready, Java 21+ Generational ZGC)**: 테라바이트 급의 힙에서도
	중단 시간(Stop-The-World)을 1ms 이하로 유지. Java 21에서는 세대별(Generational) 관리 기능이 추가되어
	효율성이 더 높아졌습니다.

### 2. GC 성능 튜닝과 JVM 옵션

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
