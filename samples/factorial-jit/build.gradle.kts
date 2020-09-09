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
    implementation("org.bytedeco:llvm-platform:10.0.1-1.5.4")
    // For standalone usage, uncomment this line...
    // implementation("com.github.vexelabs:bitbuilder:-SNAPSHOT")

    // And comment out this one
    implementation(fileTree("../../build/libs"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
