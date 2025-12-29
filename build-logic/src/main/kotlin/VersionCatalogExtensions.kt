import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.buildlogicLibs: VersionCatalog
    get() {
        val catalogs = extensions.getByType<VersionCatalogsExtension>()
        return catalogs.find("buildlogicLibs").orElseGet {
            catalogs.find("libs").orElseThrow {
                IllegalStateException("Neither 'buildlogicLibs' nor 'libs' version catalog found.")
            }
        }
    }

