# 리플렉션 (Reflection)과 동적 프로그래밍

리플렉션은 실행 중인 자바 프로그램이 자신의 내부 구조를 검사하고 수정할 수 있게 해주는 강력한 기능입니다. Spring, Hibernate 같은 대규모 프레임워크의 근간이 됩니다.

## 1. 리플렉션의 활용

- **의존성 주입 (DI)**: 런타임에 객체를 생성하고 private 필드에 의존성을 주입합니다.
- **프록시 기반 AOP**: 인터페이스나 클래스를 동적으로 구현하여 트랜잭션, 로깅 등을 처리합니다.
- **동적 로딩**: 설정 파일 등에 명시된 클래스 이름을 기반으로 클래스를 로드합니다.

## 2. 실전 코드 예시: 리플렉션으로 Private 필드 접근

```java
import java.lang.reflect.Field;

public class ReflectionExample {
    public static void main(String[] args) throws Exception {
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
