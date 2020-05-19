package dev.supergrecko.kllvm.test

import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt

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
