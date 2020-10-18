import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("maven")
    id("maven-publish")
    id("signing")
}

group = "io.vexelabs"
version = "0.1.0-SNAPSHOT"

kotlin.explicitApi()
ktlint.debug.set(true)

val isSnapshot = version.toString().endsWith("SNAPSHOT")

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://jcenter.bintray.com")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("org.bytedeco:llvm-platform:10.0.1-1.5.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.10")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.11")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.11")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test>().configureEach {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    dokkaHtml {
        outputDirectory = "$buildDir/javadoc"
        dokkaSourceSets.configureEach {
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

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
    dependsOn(tasks.dokkaHtml)
}

publishing {
    publications {
        create<MavenPublication>("vexelabs") {
            from(components["kotlin"])
            artifact(sourcesJar)
            artifact(javadocJar)

            repositories {
                maven {
                    url = if (isSnapshot) {
                        uri("https://repo.vexelabs.io/snapshots/")
                    } else {
                        uri("https://repo.vexelabs.io/")
                    }

                    credentials {
                        username = project.properties.getOrDefault("publishRepository.username", "default") as? String
                        password = project.properties.getOrDefault("publishRepository.password", "default") as? String
                    }
                }
            }

            pom {
                name.set("BitBuilder")
                description.set("Kotlin interface to the LLVM APIs")
                url.set("https://vexelabs.io/projects/bitbuilder")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("supergrecko")
                        name.set("Mats Larsen")
                        email.set("me@supergrecko.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/vexelabs/bitbuilder.git")
                    developerConnection.set("scm:git:ssh://git@github.com:vexelabs/bitbuilder.git")
                    url.set("https://github.com/vexelabs/bitbuilder")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["vexelabs"])
}
