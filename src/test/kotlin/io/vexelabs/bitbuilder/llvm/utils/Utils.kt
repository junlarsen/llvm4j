package io.vexelabs.bitbuilder.llvm.utils

internal fun <T> runAll(
    vararg subjects: T,
    handler: (item: T, index: Int) -> Unit
) {
    for ((k, v) in subjects.withIndex()) {
        handler(v, k)
    }
}
