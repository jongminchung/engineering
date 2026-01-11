import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

tasks.register("bunInstall", Exec::class.java) {
    group = "build"
    description = "Install dependencies using bun"

    inputs.file("package.json")
    commandLine = listOf("bash", "-lc", "bun install")
    outputs.dir("node_modules")
}

idea.project.settings {
    taskTriggers {
        afterSync(tasks.getByName("bunInstall"))
    }
}
