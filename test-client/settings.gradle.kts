pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}


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

rootProject.name = "test-client"
