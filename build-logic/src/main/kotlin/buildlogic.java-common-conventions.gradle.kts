@file:Suppress("UnstableApiUsage")

plugins {
    java
    id("com.diffplug.spotless")
    id("io.github.jongminchung.spotless.convention")
    id("buildlogic.java-test-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(buildlogicLibs.findLibrary("jspecify").get())
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(buildlogicLibs.findVersion("java").get().requiredVersion.toInt())
    }
}
