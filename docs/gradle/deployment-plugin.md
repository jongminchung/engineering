# Gradle Convention Plugin 배포 가이드

이 문서는 `build-logic` 프로젝트를 Gradle Plugin Portal 또는 사내 Maven 저장소에 배포하고 사용하는 방법을
설명합니다.

## 1. 배포 구조 (One JAR, Multiple Plugins)

현재 빌드 로직은 하나의 프로젝트(`build-logic`)에서 여러 개의 컨벤션 플러그인을 제공하는 구조입니다.
배포 시에는 하나의 JAR 파일이 생성되지만, 각 플러그인 ID별로 **Plugin Marker Artifact**가 생성되어 사용자는
독립적으로 플러그인을 가져다 쓸 수 있습니다.

### 포함된 플러그인 목록

- `buildlogic.java-common-conventions`
- `buildlogic.java-library-conventions`
- `buildlogic.java-test-conventions`
- `buildlogic.spring-boot-conventions`
- `buildlogic.testcontainers-conventions`
- `buildlogic.mysql-study-conventions`
- `buildlogic.kafka-study-conventions`
- `buildlogic.odata-library-conventions`

## 2. 배포 설정 (`build-logic/build.gradle.kts`)

배포를 위해 `maven-publish`와 `com.gradle.plugin-publish` 플러그인이 설정되어 있습니다.

- **Group:** `io.github.jongminchung`
- **Version:** `0.0.1` (배포 시 업데이트 필요)

## 3. 배포 방법

### Gradle Plugin Portal 배포

1. [Gradle Plugin Portal](https://plugins.gradle.org/) 계정 생성 및 API 키 발급
2. `~/.gradle/gradle.properties`에 발급받은 키 설정:

   ```properties
   gradle.publish.key=your-key
   gradle.publish.secret=your-secret
   ```

3. 배포 명령 실행:

   ```bash
   ./gradlew :build-logic:publishPlugins
   ```

### 로컬 Maven 저장소 배포 (테스트용)

```bash
./gradlew :build-logic:publishToMavenLocal
```

## 4. 클라이언트 프로젝트에서의 사용 방법

### 저장소 설정
배포된 저장소가 Gradle Portal이 아닌 경우 `settings.gradle.kts`에 추가합니다.

```kotlin
// settings.gradle.kts
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    // 사내 저장소 예시
    // maven { url = uri("https://your-repo.com/maven") }
  }
}
```

### 플러그인 적용

```kotlin
// build.gradle.kts
plugins {
  id("buildlogic.java-common-conventions") version "0.0.1"
  id("buildlogic.mysql-study-conventions") version "0.0.1"
}
```

### 번들된 Version Catalog 사용

플러그인을 안정적으로 사용하려면 플러그인이 제공하는 Version Catalog를 클라이언트 프로젝트에 등록해야 합니다.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("buildlogicLibs") {
            from("io.github.jongminchung:build-logic-catalog:0.0.1")
        }
    }
}
```

플러그인을 적용하면 컨벤션 스크립트 내부에서 위에서 등록한 `buildlogicLibs`를 참조하게 됩니다. 프로젝트 내에서 직접 참조가 필요한 경우 다음과 같이 사용합니다.

```kotlin
dependencies {
  implementation(buildlogicLibs.findLibrary("mysql").get())
}
```

## 5. 핵심 동작 원리 (Bundle Catalog)
플러그인 JAR 내부의 `META-INF/gradle/buildlogic-libs.versions.toml` 경로는 Gradle에 의해 특별하게
취급됩니다.
플러그인이 프로젝트에 적용되는 순간, 이 TOML 파일이 프로젝트의 `VersionCatalog`로 자동 등록되어 컨벤션 스크립트와 사용자
프로젝트 모두에서 동일한 버전 정보를 공유할 수 있게 됩니다.
