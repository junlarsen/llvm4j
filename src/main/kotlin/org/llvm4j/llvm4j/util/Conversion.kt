package org.llvm4j.llvm4j.util

internal fun Boolean.toInt(): Int = if (this) {
    1
} else {
    0
}

internal fun Int.toBoolean(): Boolean = this == 1