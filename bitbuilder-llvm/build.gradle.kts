plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0-rc")
    implementation("org.bytedeco:llvm-platform:9.0.0-1.5.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.0-rc")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.11")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.11")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.0-rc")
}

kotlin {
    explicitApi()
}

