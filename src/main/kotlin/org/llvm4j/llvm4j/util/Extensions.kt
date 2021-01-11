package org.llvm4j.llvm4j.util

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

internal fun Boolean.toInt(): Int = if (this) {
    1
} else {
    0
}

internal fun Int.toBoolean(): Boolean = this == 1

internal inline fun <reified T : Pointer> List<T>.toPointerPointer(): PointerPointer<T> {
    return PointerPointer(*toTypedArray())
}
