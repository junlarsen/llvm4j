package io.vexelabs.bitbuilder.internal

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

/**
 * Implements [Iterable.map] for [PointerPointer]
 *
 * The method will iterate over every item inside the [PointerPointer] and
 * run [apply] to each of the elements.
 *
 * @see Iterable.map
 */
internal inline fun <reified P : Pointer, R> PointerPointer<P>.map(
    apply: (elem: P) -> R
): List<R> {
    val res = mutableListOf<R>()

    for (i in 0 until capacity()) {
        val item = get(P::class.java, i)

        res += apply(item)
    }

    return res
}

/**
 * Util function to convert kotlin [Boolean] to [Int]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Boolean.toLLVMBool() = if (this) 1 else 0

/**
 * Util function to convert kotlin [Int] to [Boolean]
 *
 * Used because LLVM C api does not use booleans, it uses
 * C integers 1 and 0.
 */
internal fun Int.fromLLVMBool() = this > 0