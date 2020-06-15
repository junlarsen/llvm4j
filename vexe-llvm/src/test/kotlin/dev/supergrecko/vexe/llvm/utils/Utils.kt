package dev.supergrecko.vexe.llvm.utils

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.test.TestCase

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

/**
 * Cleans up a list of disposable LLVM objects, remember that this list
 * should be in FILO order. The disposable with the oldest lifetime goes last
 * due to possible dependencies
 */
internal fun TestCase.cleanup(vararg item: Disposable) {
    item.forEach { it.dispose() }
}