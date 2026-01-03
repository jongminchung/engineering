# cache

## Gradle Build Cache

Gradle 빌드 캐시는 **기능 활성화 여부**와 **캐시 저장소 설정**이 분리되어 있음.
전역 빌드 캐시와 로컬 빌드 캐시는 서로 다른 개념임.

---

### 1. 전역 빌드 캐시(Build Cache 기능)

```properties
org.gradle.caching=true
```

- gradle의 **빌드 캐시 기능 자체를 활성화**하는 설정임
- task output 캐싱을 사용할 수 있는 상태가 됨
- 이 설정이 꺼져 있으면:
  -buildCache { ... } 설정은 전부 무시됨
- 저장 위치나 방식은 아직 결정되지 않음

👉 흔히 말하는 전역 빌드 캐시 ON/OFF에 해당함

### 2. 로컬 빌드 캐시(Local Build Cache)

```kotlin
buildCache {
    local {
        enabled = true
    }
}
```

- 빌드 캐시를 **어디에 저장할지**에 대한 설정임
- 로컬 빌드 캐시는:
    - 사용자 로컬 디스크에 저장됨
    - 기본 위치: `~/.gradle/caches/build-cache-1`
- task 결과를 로컬에서 재사용함

👉 흔히 말하는 로컬 빌드 캐시

### 3. CI 환경 분기 설정 예시

```kotlin
val isCI = System.getenv("CI") == "true"

buildCache {
    local {
        enabled = !isCI
    }
}
```
