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
    localPath.set("/Applications/IntelliJ IDEA.app/Contents")

    plugins.set(listOf("java"))
}
dependencies {
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}



tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
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
