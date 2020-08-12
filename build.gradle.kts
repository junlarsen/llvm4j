plugins {
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("org.jetbrains.kotlin.jvm") version "1.4.0-rc"
}

group = "io.vexelabs"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://jcenter.bintray.com")
    }
}

tasks.dokkaHtml {
    outputDirectory = "$buildDir/dokka"
    dokkaSourceSets {
        configureEach {
            skipDeprecated = false
            includeNonPublic = false
            reportUndocumented = true
            platform = "JVM"
            jdkVersion = 8
            noStdlibLink = false
            noJdkLink = false
        }
    }
}