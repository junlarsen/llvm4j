# Casting Between Types

The LLVM type hierarchy is fairly complex, nesting parent classes multiple
levels deep. Bitbuilder attempts to mirror the LLVM C++ API's type hierarchy
which means the library also has some pretty deep inheritance paths.

Because of how types work in general, we might receive a `Value` which we're
certain is a `FunctionValue`, but the Kotlin type system will obviously not
allow us to use this anywhere a `FunctionValue` is required. To solve this, the
library ships a set of helper methods to cast between compatible values.

Using the functions `cast`, `castOrElse` and `castOrNull`, defined in the
[Casting.kt][internal-casting-kt] file we're able to turn our `Value` into a
`FunctionValue`. How this is achieved is outside the scope of this document, but
the general idea is passing the original `Value` into the `FunctionValue`
constructor.

The most basic operation, `cast` will attempt to cast the provided value to
the provided type, throwing an `IllegalArgumentException` if it fails to do so.

```kotlin
val value: Value = ...
val function: FunctionValue = cast<FunctionValue>(value)

doSomething(function)
```

The behavior of `castOrElse` and `castOrNull` are fairly self-explanatory.

[internal-casting-kt]: https://github.com/vexelabs/bitbuilder/blob/master/src/main/kotlin/io/vexelabs/bitbuilder/internal/Casting.kt