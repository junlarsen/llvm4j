plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("org.jetbrains.kotlin.jvm") version "1.4.0-rc"
    id("maven")
}

group = "io.vexelabs"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://jcenter.bintray.com")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0-rc")
    implementation("org.bytedeco:llvm-platform:10.0.0-1.5.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.0-rc")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.11")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.11")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.0-rc")
}

kotlin {
    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform {
        includeEngines("spek2")
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
