package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.llvm.utils.runAll
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class ContextTest : TestSuite({
    describe("Attempting to dispose twice fails") {
        val ctx = Context()
        ctx.dispose()
    }

    describe("Mutating discard value names") {
        val ctx = Context()

        runAll(true, false) { it, _ ->
            ctx.setDiscardValueNames(it)
            assertEquals(it, ctx.isDiscardingValueNames())
        }

        cleanup(ctx)
    }
})
