<br />
<p align="center">
  <a href="https://github.com/vexelabs/bitbuilder">
    <img src="docs/static/bitbuilder512x512.png" 
         alt="Logo" 
         width="160" 
         height="160">
  </a>

  <h3 align="center">BitBuilder</h3>

  <p align="center">
    The missing link between Kotlin and LLVM
    <br />
    <a href="https://docs.vexelabs.io/bitbuilder/index.html">
        <strong>Read the docs»</strong>
    </a>
    <br />
    <br />
    <a href="https://github.com/vexelabs/bitbuilder/issues">Report Bug</a>
    ·
    <a href="https://github.com/vexelabs/bitbuilder/issues">Request Feature</a>
  </p>
</p>

There were no robust options for accessing LLVM's C API from Kotlin. BitBuilder 
is a solid library for interacting with LLVM, written in idiomatic Kotlin.

## Table of Contents

- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [License](#license)
- [Contact](#contact)

## Getting Started

### Prerequisites

BitBuilder runs on the Java Virtual Machine and its builds are built for Java
version 8 or higher.

- Java 8 (or higher)
- Maven or Gradle

BitBuilder does not require an installation of LLVM on the hosts machine because
we're using [JavaCPP Presets][llvm-presets] which provide pre-built binaries for
the library.

> If you need to interact with the library using your own binaries, see the
> documentation for this. (Advanced Recipes > Custom LLVM Binaries)

### Installation

Because BitBuilder uses Gradle as its build system, we'll use Gradle for the
following examples, but the same concepts applies to other build systems like
Gradle, Ivy or Sbt.

BitBuilder's release artifacts are deployed to Central and its snapshot
artifacts are deployed to Sonatype OSSRH.

###### Add the necessary repositories to your Gradle build

```kotlin
repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}
```

2. Add the BitBuilder artifact.

```kotlin
dependencies {
    implementation("io.vexelabs:bitbuilder:0.1.0-SNAPSHOT")
}
```

## Usage

BitBuilder is still in the development stage so documentation is scarce as this
is still in the works. Because of the project's current state you should not
consider any of the APIs stable.

There are samples hosted in the `/samples` directory, but some of these may not
be up-to-date as the project has a rapid development cycle in these early

Documentation for the library is automatically built on each push to master and
automatically deployed to these endpoints:

- [Project Documentation][docs]
- [API Documentation (Dokka)][apidocs]

## License

This project is licensed under the Apache 2.0 License. See `LICENSE` for more
information.

## Contact

- [@supergrecko](https://twitter.com/supergrecko) - me@supergrecko.com

[llvm-presets]: https://github.com/bytedeco/javacpp-presets/tree/master/llvm
[apidocs]: https://apidocs.vexelabs.io/bitbuilder/index.html
[docs]: https://docs.vexelabs.io/bitbuilder/-bit-builder/index.html