plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.0-rc"
    id("application")
}

application {
    mainClass.set("io.vexelabs.examples.FactorialJITKt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://jcenter.bintray.com")
    maven("https://repo.vexelabs.io/snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0-rc")
    implementation("org.bytedeco:llvm-platform:10.0.1-1.5.4")
    implementation("io.vexelabs:bitbuilder:0.1.0-SNAPSHOT")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
