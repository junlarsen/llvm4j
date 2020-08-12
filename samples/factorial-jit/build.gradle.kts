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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0-rc")
    implementation("org.bytedeco:llvm-platform:9.0.0-1.5.2")
    implementation("com.github.vexelabs:bitbuilder:-SNAPSHOT")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        jvmTarget = "1.8"
    }
}