package dev.supergrecko.kllvm.annotations

/**
 * Implies that the target function is shared
 *
 * Multiple objects in the LLVM share functions across different types. This means multiple classes may require access
 * to the same method. Functions annotated with this imply that they share their implementation with something else
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class Shared