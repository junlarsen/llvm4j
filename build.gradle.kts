import java.net.URL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.Platform

plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.jetbrains.dokka") version "1.4.10.2"
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
    mavenLocal()
    jcenter()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("org.bytedeco:llvm-platform:11.0.0-1.5.5-SNAPSHOT")

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

    dokkaHtml.configure {
        outputDirectory.set(file("$buildDir/javadoc"))
        moduleName.set("BitBuilder")

        dokkaSourceSets.configureEach {
            skipDeprecated.set(false)
            includeNonPublic.set(false)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            displayName.set("JVM")
            platform.set(Platform.jvm)
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/vexelabs/bitbuilder/blob/master/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
            jdkVersion.set(8)
            noStdlibLink.set(false)
            noJdkLink.set(false)
            externalDocumentationLink {
                url.set(URL("http://bytedeco.org/javacpp-presets/llvm/apidocs/"))
            }
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
