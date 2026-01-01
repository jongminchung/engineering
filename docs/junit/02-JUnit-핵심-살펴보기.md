# JUnit 핵심 살펴보기

JUnit 5의 테스트 작동 원리와 JUnit 생애 주기에 대해 자세히 알아본다.

- **테스트 클래스**(Test Class): 하나 이상의 테스트 메서드를 포함하는 클래스. (`@Nested` 애노테이션이 붙은 내부 클래스도 포함.)
- **테스트 메서드**(Test Method): `@Test`, `@RepeatedTest`, `@ParameterizedTest`, `@TestFactory`, `@TestTemplate` 애노테이션을 사용하여
  정의한 메서드.
- **생애 주기 메서드**(Lifecycle Method): 테스트 메서드 실행 전후에 실행되는 메서드. `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`
  애노테이션을 사용하여 정의.

JUnit 5를 구동하는 데 필요한 최소한의 의존성이 `junit-jupiter-api`, `junit-jupiter-engine`이다.

`junit-vintage-engine`은 이제 JUnit 6부터 JUnit 4 지원을 하지 않기에 이를 제외한다.

---

## JUnit의 테스트 메서드 실행 원리

JUnit은 테스트 메서드의 격리성을 보장하고 테스트 코드에서 의도치 않은 부수 효과를 방지하기 위해, `@Test` 메서드를 호출하기 전에 테스트 클래스 인스턴스를 매번 새로 만든다.

- 테스트는 실행 순서에 관계없이 동일한 결과를 도출해야함.
- `@TestInstance(Lifecycle.PER_CLASS)`
- `static` 필드는 테스트 메서드간 공유가 된다는 점을 주의해야 함.

테스트를 분리하여 실행하고 싶다면 `@Tag`를 사용해서 격리해야 한다.

## JUnit 에서 제공하는 테스트 메서드 애너테이션

- `@Test`
- `@RepeatedTest`
- `@ParameterizedTest`
- `@TestFactory`
- `@TestTemplate`

## JUnit 5의 의존성 주입

JUni 5부터는 생성자와 메서드에서 파라미터를 가질 수 있도록 허용한다.
다만 의존성 주입으로 해결해야한다.

`ParameterResolver` 인터페이스는 런타임에 파라미터를 동적으로 리졸브를 한다.

현재 JUnit 5에는 세 개의 리졸버가 기본으로 내장되어 있다. 만약 다른 파라미터 리졸버를 사용하려면 `@ExtendWith`를 가지고 extension을 적용하여
파라미터 리졸버를 명시해야 한다.

기본으로 사용하는 파라미터 리졸버는 세 개이다.

- `TestInfoParameterResolver`
- `TestReporterParameterResolver`
- `RepetitionInfoParameterResolver`
