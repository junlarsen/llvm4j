package dev.supergrecko.kllvm.utils

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

/**
 * Extract all [T] out of the [PointerPointer]
 *
 * @param T Expected type from Pointer
 */
internal inline fun <reified P : Pointer> PointerPointer<P>.getAll(): List<P> {
    val res = mutableListOf<P>()

    for (i in 0..this.capacity()) {
        res += get(i) as P
    }

    return res
}