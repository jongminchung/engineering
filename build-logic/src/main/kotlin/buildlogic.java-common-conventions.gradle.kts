@file:Suppress("UnstableApiUsage")

plugins {
    java
    id("buildlogic.java-test-conventions")
    id("buildlogic.formatter-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProjectLibs.findLibrary("jspecify").get())
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(rootProjectLibs.findVersion("java").get().requiredVersion.toInt())
    }
}
