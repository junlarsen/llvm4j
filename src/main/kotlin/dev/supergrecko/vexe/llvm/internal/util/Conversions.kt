package dev.supergrecko.vexe.llvm.internal.util

import org.bytedeco.javacpp.Pointer

/**
 * Util function to convert kotlin [Int] to [Boolean]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Int.fromLLVMBool() = this > 0

/**
 * Util function to convert kotlin [Boolean] to [Int]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Boolean.toLLVMBool() = if (this) 1 else 0
