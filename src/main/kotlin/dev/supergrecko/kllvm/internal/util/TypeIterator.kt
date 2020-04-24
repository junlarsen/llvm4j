package dev.supergrecko.kllvm.internal.util

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

/**
 * Helper method to turn a [PointerPointer] into a [List] of [R]
 *
 * The method will iterate over every item inside the [PointerPointer] and
 * run [apply] to each of the elements.
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
