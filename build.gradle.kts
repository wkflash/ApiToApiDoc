plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.wk.plugin"
version = "1.0-SNAPSHOT"

repositories {
   // mavenCentral()
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
}

intellij {
    //localPath.set("/Applications/IntelliJ IDEA.app/Contents")
    localPath.set("D:\\Development Tools\\IntelliJ IDEA 2023.3.8")

    plugins.set(listOf("java"))
}
dependencies {
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}



tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from("src/main/resources") {
            include("**/*")
        }
        // 设置资源文件编码
        charset("UTF-8")
    }
    withType<JavaCompile> {
        options.apply {
            encoding = "UTF-8"
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
    }
    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

}
