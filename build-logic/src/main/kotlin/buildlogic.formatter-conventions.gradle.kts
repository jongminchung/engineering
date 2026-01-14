plugins {
    id("com.diffplug.spotless")
}

spotless {
    java {
        palantirJavaFormat("2.84.0")

        formatAnnotations()
        removeUnusedImports()
        trimTrailingWhitespace()

        importOrder("java", "jakarta", "org", "com", "net", "io", "lombok")

        targetExclude("**/build/**")
    }

    kotlin {
        ktlint("1.8.0")

        trimTrailingWhitespace()
    }

    kotlinGradle {
        ktlint("1.8.0")

        trimTrailingWhitespace()
    }
}
