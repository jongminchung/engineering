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

---

## configuration-cache, build-cache 원칙

```properties
# gradle.properties
# configuration-cache
org.gradle.configuration-cache=true
# build-cache
org.gradle.caching=true
```

### 1. build.gradle.kts: 구성 단계에서 외부 프로세스/파일 I/O/즉시 평가(get) 하는 최악 vs Provider 지연평가로 푸는 최선

```kotlin
// build.gradle.kts (WORST)

// 1. 구성 단계에서 외부 프로세스 실행 (git)
val gitSha: String = run {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = out
    }
    out.toString().trim()
}

// 2. 구성 단계에서 파일 I/O
val versionTxt: String = file("version.txt").readText().trim()

// 3. Provider를 즉시 평가(get) - 구성 단계에서 값 확정
val apiUrl: String = providers.gradleProperty("API_URL").get()

version = "$versionTxt+$gitSha"

tasks.register("printInfo") {
    doLast {
        println("apiUrl=$apiUrl, version=$version")
    }
}
```

**문제점 요약**

- `exec {}` + `readText()` 가 구성 단계에서 실행됨 → CC 재사용 불가/불안정
- `gradleProperty(...).get()`도 구성 단계에서 값 확정 → 캐시/증분빌드에 불리
- 결과적으로 CC hit율 낮고, 환경에 따라 항상 재구성/재계산 가능성 큼

```kotlin
// build.gradle.kts (BEST)

plugins {
    base
}

// 1) 외부값은 Provider로 "핸들"만 잡아두고 지연
val apiUrlProvider = providers.gradleProperty("API_URL")
    .orElse("https://example.invalid")

// 2) 파일 내용도 가능한 "입력"으로 태스크에 연결
val versionFile = layout.projectDirectory.file("version.txt")

// 3) git sha 같은 외부 프로세스는 "태스크 실행 시점"으로 이동
abstract class GitShaTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        val out = java.io.ByteArrayOutputStream()
        project.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = out
        }
        outputFile.get().asFile.writeText(out.toString().trim())
    }
}

val writeGitSha = tasks.register<GitShaTask>("writeGitSha") {
    outputFile.set(layout.buildDirectory.file("git-sha.txt"))
}

// version 조합도 태스크 기반으로 (여기서는 단순 예시로 print 태스크에서 조합)
tasks.register("printInfo") {
    // 태스크 입력으로 선언되게 연결하면 BC/증분에 유리
    inputs.file(versionFile)
    inputs.file(writeGitSha.flatMap { it.outputFile })
    inputs.property("apiUrl", apiUrlProvider)

    doLast {
        val versionTxt = versionFile.asFile.readText().trim()
        val gitSha = writeGitSha.get().outputFile.get().asFile.readText().trim()
        val apiUrl = apiUrlProvider.get()

        println("apiUrl=$apiUrl")
        println("version=$versionTxt+$gitSha")
    }
}
```

**포인트 요약**

- 구성 단계에서는 “값 계산” 안 하고 Provider/파일 핸들만 잡음
- 외부 프로세스는 태스크 실행 시점으로 이동
- 태스크 inputs/outputs 연결로 BC(build-cache)에도 이득(재실행/캐시 조건이 명확)

### 2. 커스텀 태스크: 입력/출력 선언 안 해서 BC 못 타는 최악 vs 완전 선언 + 결정적 출력으로 캐시 타는 최선

**worst case: BC 거의 못 탐 + 결과 흔들림**

```kotlin
// build.gradle.kts (WORST)

tasks.register("generateReport") {
    doLast {
        // 입력 선언 없음
        val src = file("src/main/resources/data.txt")

        // 출력 위치/파일 선언 없음
        val out = file("$buildDir/reports/report.txt")

        // 비결정성: 시간 포함
        out.parentFile.mkdirs()
        out.writeText("generatedAt=${System.currentTimeMillis()}\n" + src.readText())
    }
}
```

**문제점임**

- 입력/출력 선언이 없어서 캐시 조건 추적 불가
- 시간 값 때문에 입력 같아도 출력이 항상 달라짐 → BC 적중 불가에 가까움

**best case: (BC 잘탐 + 재현 가능)**

```kotlin
// build.gradle.kts (BEST)

abstract class GenerateReport : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    // 결정적 출력: 옵션이 있으면 @Input 으로 선언
    @get:Input
    abstract val header: Property<String>

    @TaskAction
    fun generate() {
        val inText = inputFile.get().asFile.readText()
        val outFile = outputFile.get().asFile
        outFile.parentFile.mkdirs()

        // 시간/랜덤/절대경로 등 비결정 요소 제거
        outFile.writeText("${header.get()}\n$inText")
    }
}

tasks.register<GenerateReport>("generateReport") {
    inputFile.set(layout.projectDirectory.file("src/main/resources/data.txt"))
    outputFile.set(layout.buildDirectory.file("reports/report.txt"))
    header.convention("report-v1")
}
```

**포인트임**

- @InputFile/@OutputFile/@Input 명확히 선언 → BC/증분빌드 추적 가능
- PathSensitivity.RELATIVE로 경로 환경차를 줄여 캐시 재사용성 개선
- 비결정 요소 제거로 캐시 hit 가능성 상승

### 3. tasks.register vs getByName: configuration avoidance 최악 vs 최선

**worst case**

```kotlin
// build.gradle.kts (WORST)

val jarTask = tasks.getByName("jar") // 즉시 실체화 됨
jarTask.doLast {
    println("jar done")
}
```

- Configuration Cache는 실체화된 태스크의 상태도 직렬화하여 저장할 수 있음
- 이 코드 자체가 캐시 생성을 막거나 오류를 발생시키지는 않음
- 문제는 **성능**. `getByName`을 사용하면 해당 태스크가 실제로 실행되지 않더라도 Gradle의 구성 단계(
  Configuration Cache)에서
  태스크 객체가 즉시 생성됨.

#### 확인

```kotlin
tasks.getByName("jar") {
    println("--- jar 태스크가 즉시 생성됨")
}
```

```bash
# jar와 상관없는 help 태스크만 실행
./gradlew :help
```

결과: `jar`태스크를 실행하지 않았음에도 로그가 출력됩니다. 즉, 빌드 구성 단계에서 이미 객체가 생성되었음을 의미합니다.

```kotlin
tasks.named("jar") {
    println("--- jar 태스크가 즉시 생성됨")
}
```

결과: 로그가 출력되지 않음. `jar` 태스크가 실행되어야 그제서야 로그가 출력된다.

**결론:**

`getByName`은 빌드 구성 단계에서 항상 태스크 객체를 만들어내기 때문에 성능에 악영향을 줄 수 있습니다.
반면 `named`는 필요할 때만 객체를 생성(Task Configuration Avoidance)하므로 대규모 프로젝트일수록 차이가 커집니다.

**best case**

```kotlin
// build.gradle.kts (BEST)
tasks.named("jar") {
    doLast {
        println("jar done")
    }
}
```

### 4. settings.gradle.kts: settings에서 동적 include/외부 호출하는 최악 vs 정적 구조 + 선언 중심 최선

**worst case**

```kotlin
// settings.gradle.kts (WORST)

// 외부 호출/파일 스캔 등으로 모듈을 동적으로 include (환경에 따라 흔들림)
val modules =
    file("modules.txt").readLines().map { it.trim() }.filter { it.isNotEmpty() }
modules.forEach { include(it) }
```

**best case:**

```kotlin
// settings.gradle.kts (BEST)

rootProject.name = "sample"

// 빌드 구조는 정적으로/결정적으로
include(":app")
include(":core")
include(":infra")
```

```kotlin
tasks.register("generateVersion") {
    // 1. 필요한 정보의 '출처'만 정의 (지연 처리)
    val gitShaProvider = providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText()

    val versionFile = layout.projectDirectory.file("version.txt")
    val versionTxtProvider = providers.fileContents(versionFile).asText

    // 2. 실행 단계(Execution Phase)에서 실제 로직 처리
    doLast {
        val sha = gitShaProvider.get().trim()
        val ver = versionTxtProvider.get().trim()

        project.version = "$ver+$sha"
        println("Project version set to: ${project.version}")
    }
}
```

1. Configuration Cache 최적화: providers.exec나 providers.fileContents를 사용하면 Gradle이
   해당 값을 추적할 수 있게 되어 Configuration Cache와 완벽하게 호환됩니다.
2. 빌드 속도 향상: exec를 구성 단계에서 호출하면 모든 빌드(심지어 gradle help를 칠 때도)마다 git 명령어를 실행하느라
   시간이 낭비됩니다. 위와 같이 구현하면 generateVersion 태스크가 필요할 때만 실행됩니다.
