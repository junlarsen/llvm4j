# Custom LLVM Binaries

In some cases, the production-ready binaries built by Bytedeco are not enough
for your use case, and they may be tedious to debug as they have no debug
information available. In these scenarios you might want to use your own,
self-compiled LLVM binary.

This is possible by manipulating the way JavaCPP searches for binaries. The
[JavaCPP library Loader][javacpp-library-loader] uses a set of paths for picking
up binaries. This path can be modified which will ensure your binaries get
loaded instead of the ones from Bytedeco. To set this up, you will have to
modify this path before any calls to `Loader.load()` is ran.

```kotlin
val path = System.getProperty("platform.library.path")
System.setProperty("platform.library.path", "my/custom/path;$path")
```

Adding libraries to Java's own `java.library.path` property is also an option,
but doing this does not necessarily gain precedence over the Loader's default
path.

[javacpp-library-loader]: https://github.com/bytedeco/javacpp/blob/master/src/main/java/org/bytedeco/javacpp/Loader.java
