# Bitbuilder

There were no decent options to utilize the LLVM from JVM. Bitbuilder is a
modern library for interacting with the [LLVM](https://llvm.org) from the JVM
in an idiomatic and object-oriented approach.

**Meta**

The project is still under early development. There are no published releases
of the library yet. 

## Getting Started

**Installation**

You can retrieve Bitbuilder from Jitpack.io. Add the following to your gradle
configuration.

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.vexelabs:bitbuilder:master-SNAPSHOT'
}
```

**NB**: Because Bitbuilder does not have any releases yet, the only available
build is `master-SNAPSHOT`

**Using the library**

Coming soon...

## How does this library work?

This library uses the generated LLVM bindings for Java (see [bytedeco/javacpp
-presets](https://github.com/bytedeco/javacpp-presets/tree/master/llvm)) to
create a more object-oriented approach which roughly mirrors LLVM's C++ API.

Bitbuilder aims to cover 100% of the LLVM-C API and additionally provide a
wrapper framework for using the LLVM in idiomatic Kotlin.

## License

The entire project is licensed under the Apache 2.0 license.

[Apache 2.0](LICENSE)
