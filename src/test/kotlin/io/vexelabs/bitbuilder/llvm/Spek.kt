// Eat all warnings about unused memoized variables
@file:Suppress("UNUSED_VARIABLE")

package io.vexelabs.bitbuilder.llvm

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import org.spekframework.spek2.dsl.Root

internal fun Root.setup() {
    val context by memoized(
        factory = { Context() },
    )

    val module by memoized(
        factory = { context.createModule("test.ll") },
    )

    val builder by memoized(
        factory = { context.createBuilder() },
    )

    val utils by memoized(
        factory = { TestUtils() },
        destructor = { it.destruct() }
    )
}
