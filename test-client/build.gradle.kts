plugins {
    id("buildlogic.java-common-conventions") version "0.0.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "com.example"
version = "1.0-SNAPSHOT"

// 플러그인이 주입한 카탈로그가 정상적으로 로드되었는지 확인하기 위한 태스크
tasks.register("checkCatalog") {
    doLast {
        val catalogs = extensions.getByType<VersionCatalogsExtension>()
        println("Available catalogs: ${catalogs.catalogNames}")
        val libs = catalogs.find("buildlogicLibs").get()
        println("Library mysql: ${libs.findLibrary("mysql").get()}")
    }
}
