package dev.supergrecko.vexe.llvm.utils

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.test.TestSuite

internal fun cleanup(item: Disposable) {
    item.dispose()
}

internal open class TestSuite : TestSuite<Disposable>(::cleanup)