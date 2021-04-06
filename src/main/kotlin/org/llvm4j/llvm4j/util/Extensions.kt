package org.llvm4j.llvm4j.util

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

internal fun Boolean.toInt(): Int = if (this) { 1 } else { 0 }
internal fun Int.toBoolean(): Boolean = this == 1

/** Get the byte pointer's string content and drop it afterwards */
internal fun BytePointer.take(): String {
    val copy = string
    deallocate()
    return copy
}

/** Turns a List of pointers into a PointerPointer */
internal inline fun <reified T : Pointer> List<T>.toPointerPointer(): PointerPointer<T> {
    return PointerPointer(*toTypedArray())
}

/** Represents a logic flow branch which is unreachable - use with caution */
public class SemanticallyUnreachable : RuntimeException()
