# Java 예외 처리 (Exception Handling)

## 1. 예외 계층 구조

Java의 모든 예외와 오류는 `Throwable` 클래스를 상속받으며, 다음과 같은 트리 구조를 가집니다.

```text
Object
└── Throwable
    ├── Error
    │   └── VirtualMachineError
    │       ├── OutOfMemoryError
    │       ├── StackOverflowError
    │       └── ...
    └── Exception
        ├── Checked Exception (IOException, SQLException, etc.)
        └── RuntimeException (Unchecked Exception)
            ├── NullPointerException
            ├── IndexOutOfBoundsException
            └── ...
```

- **Throwable**: 예외 계층의 최상위 클래스입니다.
- **Error**: 시스템 레벨의 심각한 오류로, 애플리케이션에서 복구하기 힘든 경우입니다. (예: `VirtualMachineError` 하위의 `OutOfMemoryError`, `StackOverflowError`)
- **Exception**: 애플리케이션 레벨에서 발생하며 복구 가능한 오류입니다.
 	- **Checked Exception**: 컴파일 시점에 체크되며, 반드시 예외 처리가 필요합니다.
 	- **RuntimeException (Unchecked Exception)**: 실행 시점에 발생하며, 명시적인 예외 처리가 강제되지 않습니다.

`Error`는 시스템적인 문제이므로 애플리케이션 레벨에서 처리하지 않는 것이 원칙입니다.

---

## 2. Checked Exception 처리 전략

Java에서 Checked Exception을 만났을 때, 무분별하게 `throws`를 남발하기보다 다음과 같은 전략을 사용하는 것이 권장됩니다.

### 예외 복구 (Exception Recovery)

예외 상황을 파악하여 적절한 조치를 취해 정상 상태로 돌려놓을 수 있는 경우입니다.

- 예: 네트워크 오류 시 재시도(Retry), 파일이 없을 때 기본 파일 사용 등.
- **주의**: 단순히 로그만 남기고 무시하는 것은 복구가 아닙니다.

### 예외 전환 (Exception Translation / Wrapping)

가방 많이 권장되는 방식으로, Checked Exception을 Unchecked Exception(RuntimeException)으로 감싸서 던지는 방식입니다.

- **이유**: 메서드 시그니처를 깔끔하게 유지하고, 호출자가 강제로 예외 처리를 하지 않아도 되게 합니다.
- **예시**:

	```java
	try {
	  // IOException 발생 가능 코드
	} catch (IOException e) {
	  throw new RuntimeException(e); // Unchecked로 전환
	}
	 ```

### 리소스 자원 반납 (Try-with-resources)

Java 7 이상에서는 예외 발생 여부와 상관없이 사용한 리소스(`InputStream`, `Connection` 등)를 확실히 닫기 위해 `try-with-resources` 문법을 사용합니다.

---

## 3. 현대적인 예외 처리 트렌드

### Checked Exception의 퇴장

최근 언어(Kotlin, Go 등)와 프레임워크(Spring)는 **Checked Exception을 지양**하는 추세입니다.

- **이유**: Checked Exception은 메서드 시그니처를 오염시키고, 호출자에게 불필요한 예외 처리 로직을 강제하여 코드의 가독성과 유연성을 떨어뜨립니다.
- **Kotlin의 경우**: Java의 Checked Exception 개념이 없으며, 모든 예외는 **Unchecked Exception (RuntimeException)**으로 취급됩니다. 컴파일 타임에 `try-catch`를 강제하지 않습니다.
