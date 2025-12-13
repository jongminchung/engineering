pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "engineering"

includeModule("odata", "odata-core", "odata-spring")

includeModule("study", "kafka", "postgresql", "coding-test")

fun includeModule(
    subdir: String,
    vararg projectPath: String,
) {
    for (project in projectPath) {
        include(project)
        project(":$project").projectDir = file("$rootDir/$subdir/$project")
    }
}
