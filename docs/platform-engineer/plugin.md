# 플러그인 아키텍처 및 시스템 정리

플러그인 시스템은 핵심 로직(Core)과 확장 로직(Extension)을 분리하여 시스템의 확장성과 유지보수성을 높이는 설계 패턴입니다. 특히
현대의 **클라우드 네이티브(Cloud Native)** 환경에서는 시스템의 구성 요소가 동적으로 변하고, 다양한 환경에 이식 가능해야 하므로
플러그인 아키텍처의 중요성이 더욱 강조되고 있습니다.

## 0. 클라우드 네이티브와 플러그인 철학

클라우드 네이티브 환경에서의 플러그인 시스템은 단순한 기능 추가를 넘어 다음과 같은 가치를 지향합니다.

1. **선언적 확장(Declarative Extension)**: "어떻게(How)" 확장할지보다 "무엇을(What)" 확장할지
   정의합니다. (예: Kubernetes CRD)
2. **느슨한 결합과 표준화(Loose Coupling & Standardization)**: 인터페이스(CSI, CNI 등)를 통해 구현체에
   의존하지 않고 교체 가능해야 합니다.
3. **동적 적응성(Dynamic Adaptability)**: 시스템 재시작 없이 런타임에 플러그인을 발견하고 적용할 수 있어야 합니다.
4. **격리와 보안(Isolation & Security)**: 확장 기능이 전체 시스템의 안정성을 해치지 않도록 샌드박싱되어야 합니다.

---

## 1. Gradle 플러그인

Gradle은 빌드 자동화 도구로, 강력한 플러그인 시스템을 갖추고 있습니다.

### 핵심 개념

- **Plugin Interface**: `org.gradle.api.Plugin<T>` 인터페이스를 구현합니다. 주로 `Project`
  객체를 대상으로 동작합니다.
- **Task**: 빌드의 작업 단위입니다. 플러그인은 새로운 Task를 정의하고 등록합니다.
- **Extension**: 사용자가 `build.gradle`에서 플러그인 설정을 커스터마이징할 수 있게 해주는 객체입니다. (예:
  `test { ... }`, `spotless { ... }`)
- **Convention**: 특정 언어나 프레임워크에 대한 기본 설정을 제공합니다. (예: Java 플러그인은 소스 폴더 구조를
  `src/main/java`로 표준화함)

### 동작 원리

1. **인스턴스화**: `apply plugin: 'id'` 호출 시 플러그인 클래스가 인스턴스화됩니다.
2. **구성(Configuration)**: 플러그인의 `apply(Project)` 메서드가 실행되어 Task를 등록하고 Extension을
   추가합니다.
3. **실행(Execution)**: 등록된 Task들이 의존 관계에 따라 실행됩니다.

---

## 2. OpenStack 플러그인 (Stevedore)

OpenStack은 Python으로 작성된 클라우드 플랫폼으로, **Stevedore**라는 라이브러리를 통해 플러그인을 관리합니다.

### 핵심 개념

- **Python Entry Points**: Python의 `setuptools`에서 제공하는 기능으로, 패키지가 제공하는 특정 서비스(
  Interface)를 외부에 노출하는 지점입니다.
- **Stevedore**: Entry Points를 동적으로 로드하고 관리하기 위한 Wrapper 라이브러리입니다.

### 플러그인 관리 방식

- **DriverManager**: 특정 이름의 단일 플러그인을 로드합니다.
- **ExtensionManager**: 특정 네임스페이스에 등록된 모든 플러그인을 로드합니다.
- **NamedExtensionManager**: 이름 목록을 기반으로 특정 플러그인들을 로드합니다.

### 특징

- **동적 로딩**: 런타임에 설치된 Python 패키지에서 플러그인을 발견하여 로드합니다.
- **느슨한 결합**: 핵심 코드는 인터페이스만 정의하고, 실제 구현체는 독립적인 패키지로 존재할 수 있습니다.

---

## 3. 언어 및 프레임워크별 플러그인 패턴

### Java (SPI: Service Provider Interface)

Java 표준 API에서 제공하는 플러그인 방식입니다.

- **ServiceLoader**: `META-INF/services/` 디렉토리에 인터페이스의 구현 클래스명을 적은 파일을 두면, 런타임에
  이를 찾아 로드합니다.
- 예: JDBC Driver, Charset 공급자 등.

---

## 4. 플랫폼 및 프레임워크별 확장 매커니즘

언어 수준의 플러그인을 넘어, 대규모 플랫폼과 프레임워크에서는 시스템 전체의 동작을 커스터마이징하기 위한 고도화된 확장 패턴을 사용합니다.

### Kubernetes (Cloud Native Extension)

쿠버네티스는 클라우드 네이티브 플러그인 시스템의 정수를 보여주며, 선언적 API와 컨트롤러 패턴을 기반으로 강력한 확장성을 제공합니다.

- **CRD (Custom Resource Definition)**: 사용자가 정의한 리소스 타입을 쿠버네티스 API에 등록하여 마치 기본
  리소스(Pod, Service 등)처럼 사용할 수 있게 합니다. 이는 시스템을 **선언적으로 확장**하는 핵심 메커니즘입니다.
- **Operator Pattern**: CRD와 커스텀 컨트롤러를 결합하여 특정 애플리케이션의 운영 지식을 코드화합니다.
- **Admission Controllers**: API 요청이 객체로 저장되기 전 가로채서 수정(Mutating)하거나 검증(
  Validating)합니다.
- **Interface (CSI, CNI, CRI)**: 스토리지, 네트워크, 컨테이너 런타임 표준 인터페이스를 통해 다양한 벤더의 구현체를
  **표준화된 플러그인** 방식으로 교체할 수 있습니다. 이는 특정 클라우드 벤더에 종속되지 않는(Vendor Agnostic) 특성을
  제공합니다.

### Spring Framework (Java Framework)

스프링은 IoC(Inversion of Control) 컨테이너의 생명주기에 개입하는 다양한 확장 지점을 제공합니다.

- **BeanPostProcessor**: 빈(Bean) 객체가 생성되고 초기화되는 시점에 로직을 가로채어 프록시를 생성하거나 값을
  변경합니다. (AOP의 기반)
- **BeanFactoryPostProcessor**: 빈 정의(Metadata)를 설정하는 단계에서 메타데이터를 수정합니다.
- **Conditional Annotations**: 특정 조건(프로퍼티 존재 여부, 클래스 존재 여부 등)에 따라 빈 등록 여부를 결정하여
  유연한 구성을 지원합니다.
- **Spring Boot Starters**: 자동 설정(Auto-configuration) 메커니즘을 통해 라이브러리 추가만으로 복잡한
  설정을 자동으로 완료하는 "Opinionated" 확장 방식을 제공합니다.

### Next.js (Web Framework)

React 기반 프레임워크인 Next.js는 빌드 타임과 런타임 모두에서 확장을 지원합니다.

- **next.config.js**: Webpack/TurboPack 설정, 환경 변수, 리다이렉션 등을 제어하는 중앙 설정 파일입니다. 많은
  플러그인들이 이 파일을 래핑하여 기능을 추가합니다.
- **Middleware**: Edge 런타임에서 요청을 가로채어 인증, 리다이렉션, 헤더 수정 등을 수행합니다.
- **Custom App/Document**: 모든 페이지의 공통 레이아웃이나 HTML 구조를 커스터마이징합니다.
- **SWC Plugins**: Rust 기반 컴파일러인 SWC에 플러그인을 추가하여 빌드 프로세스 중 코드 변환 로직을 확장합니다.

---

## 5. 플러그인 시스템의 공통 아키텍처 패턴

플러그인과 확장 시스템은 구현 방식이 다르더라도 다음과 같은 핵심 요소를 공통적으로 포함합니다. 이 요소들은 시스템이 얼마나 클라우드
네이티브한지를 결정하는 척도가 되기도 합니다.

1. **Discovery (발견)**: 시스템이 사용 가능한 플러그인을 어떻게 찾을 것인가? 클라우드 네이티브 환경에서는 서비스 디스커버리와
   연계되기도 합니다.
2. **Registration (등록)**: 발견된 플러그인을 시스템에 등록하고 메타데이터를 관리하는 과정. 런타임 등록 가능 여부가 유연성을
   결정합니다.
3. **Lifecycle Management (생명주기 관리)**: 플러그인의 로드, 초기화, 실행, 언로드 과정을 제어. 무중단 업데이트를
   위해 중요합니다.
4. **Sandboxing (샌드박싱)**: 플러그인이 핵심 시스템이나 다른 플러그인에 영향을 주지 않도록 격리. 보안과 안정성의
   핵심입니다. (예: WebAssembly 기반 샌드박싱)
5. **Communication (통신)**: 코어 시스템과 플러그인이 데이터를 주고받는 방식. 대규모 분산 환경에서는 gRPC나 메시지 큐를
   통한 원격 통신이 활용됩니다.
