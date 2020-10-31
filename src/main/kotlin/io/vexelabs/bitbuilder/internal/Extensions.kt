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

    for (i in position() until capacity()) {
        val item = get(P::class.java, i)

        res += apply(item)
    }

    return res
}

/**
 * Collapses an List of pointer types into a PointerPointer of the same size
 * as the list
 *
 * @see PointerPointer
 */
internal inline fun <reified T : Pointer> List<T>.toPointerPointer(): PointerPointer<T> {
    return PointerPointer(*toTypedArray())
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