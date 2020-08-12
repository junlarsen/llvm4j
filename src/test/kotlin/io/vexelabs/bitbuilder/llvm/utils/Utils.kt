package io.vexelabs.bitbuilder.llvm.utils

import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt

internal fun constIntPairOf(x: Int, y: Int): Pair<ConstantInt, ConstantInt> {
    val ty = IntType(32)

    return ConstantInt(ty, x) to ConstantInt(ty, y)
}

internal fun <T> runAll(
    vararg subjects: T,
    handler: (item: T, index: Int) -> Unit
) {
    for ((k, v) in subjects.withIndex()) {
        handler(v, k)
    }
}
