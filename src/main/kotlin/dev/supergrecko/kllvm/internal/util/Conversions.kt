package dev.supergrecko.kllvm.internal.util

import org.bytedeco.javacpp.Pointer

/**
 * Util function to convert kotlin [Int] to [Boolean]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Int.fromLLVMBool() = this == 1

/**
 * Util function to convert kotlin [Boolean] to [Int]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Boolean.toLLVMBool() = if (this) 1 else 0

/**
 * Wrap a nullable LLVM ref into a new type or null.
 *
 * A lot of the LLVM functions return nullable types to mimic C++'s nullptr.
 *
 * Instead of returning an if-expression this is used.
 */
internal inline fun <T : Pointer, R> wrap(item: T?, apply: (T) -> R): R? {
    return if (item == null) {
        null
    } else {
        apply(item)
    }
}