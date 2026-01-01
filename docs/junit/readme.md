# [JUnit](https://docs.junit.org/current/user-guide)

JUnit = JUnit Platform + JUnit Jupiter + JUnit Vintage

- JUnit Vintage 는 가져오는 것에서 제외 (JUnit 3, JUnit 4 사용하지 않음)

JUnit 5는 모듈 아키텍처를 가지고 있으며 JUnit 4와 다르게 jar 파일을 클래스패스에 추가하지 않는다.
JUnit 5부터는 모놀리식 아키텍처를 따르지 않는다. 또한 자바 5에 애노테이션이 도입되면서 JUnit 도 애노테이션을 사용하는 방향으로 바뀌고 있다.
이전 버전에서는 모든 테스트 클래스가 기본 클래스를 상속해야 하고 모든 테스트 메서드가 반드시 메서드명에 접두사 test를 써야 했던것과는 다르다.
